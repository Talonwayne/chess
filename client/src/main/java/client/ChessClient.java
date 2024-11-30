package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.DrawBoard;
import ui.EscapeSequences;
import websocket.WebSocketFacade;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    public boolean isLoggedIn = false;
    public boolean isInGame = false;
    public boolean isObserving = false;
    public ChessGame curGame;
    private int gameID;
    private DrawBoard display;
    private String auth;
    private ArrayList<GameData> curGameList;
    private WebSocketFacade webSocket;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        curGameList = null;
        try {
            webSocket = new WebSocketFacade(serverUrl);
        } catch (Exception e){
            System.out.print("Websocket failed to init");
        }
    }

    public String evaluate(String input){
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "1" -> highlightMoves(params);
                case "2" -> makeMove(params);
                case "3" -> redrawBoard();
                case "4" -> leave();
                case "5" -> resign();
                default -> help();
            };
        } catch (Exception e){
            return e.getMessage();
        }
    }

    public String register(String... params){
        if (params.length >= 3){
            try {
                auth = server.register(params[0], params[1], params[2]).authToken();
                isLoggedIn = true;
                return this.help();
            }catch (HttpRetryException e){
                throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Username already Exists");
            }
        }
        throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Expected: username password email");
    }

    public String login(String... params){
        if (params.length >= 2){
            try {
                auth = server.login(params[0], params[1]).authToken();
                isLoggedIn = true;
                return this.help();
            }catch (HttpRetryException e){
                throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid Username or password");
            }
        }
        throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Expected: username password");
    }

    public String quit(){
        return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Thanks for Playing";
    }

    public String help(){
        if(isInGame){
            return EscapeSequences.SET_TEXT_COLOR_GREEN + """
                    Valid Inputs
                    1 [position of the piece] - Highlight the possible moves of a piece (write the letter first like a4)
                    2 [start position] [end position] [   |QUEEN|BISHOP|KNIGHT|ROOK]"- Make a move (write the letter first like a4)
                    3 - Redraw the Chessboard
                    4 - Leave the game
                    5 - Resign the game
                    help - see possible commands
                    """;
        } else if (isLoggedIn){
            return EscapeSequences.SET_TEXT_COLOR_GREEN + """
                    Valid Inputs
                    create <NAME> - make a game
                    list - show all games
                    join <ID> [WHITE|BLACK] - join a game and pick your color
                    observe <ID> - observe a game
                    logout - exit the game
                    quit - to exit
                    help - see possible commands
                    """;
        } else {
            return EscapeSequences.SET_TEXT_COLOR_GREEN + """ 
                    Valid Inputs
                    register <USERNAME> <PASSWORD> <EMAIL> - to make an account
                    login <USERNAME> <PASSWORD> - to play
                    quit - to exit
                    help - see possible commands
                    """;
        }
    }

    public String create(String... params){
        if(!isLoggedIn){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to sign in first");
        }
        if (params.length >= 1){
            try {
                server.create(auth, params[0]);
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + params[0] + EscapeSequences.SET_TEXT_COLOR_GREEN + " created";
            } catch (HttpRetryException e){
                throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Game Name Already Exists");
            }
        }
        throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Expected: name");
    }

    public String list(){
        if(!isLoggedIn){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to sign in first");
        }
        try{
            HashSet<GameData> games = server.list(auth).games();
            curGameList = new ArrayList<>(games);
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < curGameList.size(); i++){
                GameData game = curGameList.get(i);
                output.append(i +1).append(". ").append(game.gameName()).append("- White: ").
                        append(game.whiteUsername()).append(", Black: ").append(game.blackUsername()).append("\n");
            }
            return output.toString();
        }catch (Exception e){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Likely Connection Error");
        }
    }

    public String join(String... params){
        if(!isLoggedIn){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to sign in first");
        }
        if (curGameList == null){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You need to list the games first, type list");
        }
        if (curGameList.isEmpty()){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Make a Game First");
        }
        if (params.length < 2) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Expected: ID# WHITE|BLACK");
        }

        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Expected color: WHITE or BLACK");
        }

        int realIndex = getRealGameID(params[0]);
        if (realIndex >= curGameList.size()){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "That game does not exist");
        }

        GameData gameData = curGameList.get(realIndex);
        try {
            server.join(auth, gameData.gameID(), color);
        } catch (HttpRetryException e) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "That color is already taken");
        }
        boolean isWhite = color.equals("WHITE");
        display = new DrawBoard(isWhite);
        curGame = gameData.game() != null ? gameData.game() : new ChessGame();
        gameID = gameData.gameID();
        try {
            webSocket.connectToGame(auth, gameID);
        } catch (HttpRetryException e) {
            throw new RuntimeException(EscapeSequences.SET_TEXT_COLOR_RED  + "Connection Error");
        }
        isInGame = true;
        return redrawBoard() + help();
    }

    public String observe(String... params){
        if (curGameList == null){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You need to list the games first, type list");
        }
        if (curGameList.isEmpty()){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Make a Game First");
        }
        if(!isLoggedIn){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to sign in first");
        }
        if (params.length < 1) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Expected: ID#");
        }

        int realIndex = getRealGameID(params[0]);
        if (realIndex >= curGameList.size()){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "That game does not exist");
        }
        try {
            GameData gameData = curGameList.get(realIndex);
            curGame = gameData.game() != null ? gameData.game() : new ChessGame();
            gameID = gameData.gameID();
            webSocket.connectToGame(auth,gameID);
            isObserving = true;
            isInGame = true;
            return redrawBoard();
        } catch (Exception e){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "That game does not exist");
        }
    }

    public String logout(){
        if(!isLoggedIn){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to sign in first");
        }
        try {
            server.logout(auth);
            auth = null;
            isLoggedIn = false;
            return EscapeSequences.SET_TEXT_COLOR_YELLOW + "You are logged out";
        } catch (HttpRetryException e){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to sign in first");
        }
    }

    private int getRealGameID(String fakeID){
        return Integer.parseInt(fakeID) - 1;
    }


    public String redrawBoard(){
        if (!isInGame){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to join a game first");
        }
        if (isObserving){
            display.drawBoard(curGame);
            display.setWhite(true);
            display.drawBoard(curGame);
        } else {
            display.drawBoard(curGame);
        }
        return "";
    }

    public String leave(){
        if (!isInGame){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to join a game first");
        }
        try {
            webSocket.leaveGame(auth, gameID);
        } catch (HttpRetryException e) {
            throw new RuntimeException(EscapeSequences.SET_TEXT_COLOR_RED  + "Connection Error");
        }
        isInGame = false;
        return help();
    }

    public String makeMove(String ... params){
        if (!isInGame){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to join a game first");
        }
        if (params.length < 2) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Expected: StartPosition[a4] EndPosition[a5] PromotionType[ |QUEEN|BISHOP|KNIGHT|ROOK]");
        }

        String start = params[0];
        String end = params[1];
        ChessPiece.PieceType promo = null;
        if (params.length > 2){
            String type = params[2].toUpperCase();
            switch (type){
                case "QUEEN" -> promo = ChessPiece.PieceType.QUEEN;
                case "ROOK" -> promo = ChessPiece.PieceType.ROOK;
                case "BISHOP" -> promo = ChessPiece.PieceType.BISHOP;
                case "KNIGHT" -> promo = ChessPiece.PieceType.KNIGHT;
                default -> throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Expected: StartPosition[a4] EndPosition[a5] PromotionType[ |QUEEN|BISHOP|KNIGHT|ROOK]");
            }
        }

        ChessMove move = new ChessMove(readPosition(start),readPosition(end),promo);
        try {
            webSocket.makeMove(auth, gameID, move);
        } catch (HttpRetryException e) {
            throw new RuntimeException(EscapeSequences.SET_TEXT_COLOR_RED  + "Connection Error");
        }
        redrawBoard();
        return "";
    }

    public String resign(){
        if (!isInGame){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to join a game first");
        }
        System.out.print("Are you Sure you want to Resign? [yes] | [no]");
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextLine().equals("yes")){
            try {
                webSocket.resignGame(auth, gameID);
            } catch (HttpRetryException e) {
                throw new RuntimeException(EscapeSequences.SET_TEXT_COLOR_RED  + "Connection Error");
            }
        }

        return "";
    }

    public String highlightMoves(String ... params){
        if (!isInGame){
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "You got to join a game first");
        }
        if (params.length < 1 || params[0].length() < 2) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Expected: Position (Ex. a4)");
        }

        ChessPosition pos =  readPosition(params[0]);
        display.displayPossibleMoves(pos);
        return "";
    }

    private ChessPosition readPosition(String input){
        if (input.length() != 2) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Invalid input! Expected a position like 'a4'.");
        }
        int col;

        switch (input.charAt(0)){
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
            default -> throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid row! Expected a letter a-c");
        }
        int row = input.charAt(1) - '0';
        if (row < 1 || row > 8) {
            throw new IllegalArgumentException(
                    "Invalid row! Expected a number between 1 and 8."
            );
        }
        return new ChessPosition(row,col);
    }

    public void setCurGame(ChessGame curGame) {
        this.curGame = curGame;
    }
}
