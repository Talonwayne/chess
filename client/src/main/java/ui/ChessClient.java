package ui;

import chess.ChessGame;
import model.GameData;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ChessClient {
    private final ServerFacade server;
    public boolean isLoggedIn = false;
    private String auth;
    private ArrayList<GameData> curGameList;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        curGameList = null;
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
        if (isLoggedIn){
            return """
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
            return """
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
        DrawBoard drawBoard = new DrawBoard(isWhite);
        ChessGame game = gameData.game() != null ? gameData.game() : new ChessGame();
        drawBoard.drawBoard(game);
        return "";
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
            ChessGame game = gameData.game() != null ? gameData.game() : new ChessGame();
            DrawBoard displayBoard = new DrawBoard(true);
            displayBoard.drawBoard(game);
            displayBoard.setWhite(false);
            displayBoard.drawBoard(game);
            return "";
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
}
