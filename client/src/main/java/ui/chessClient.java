package ui;

import chess.ChessGame;
import model.GameData;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class chessClient {
    private final ServerFacade server;
    public boolean isLoggedIn = false;
    private String auth;
    private ArrayList<GameData> curGameList;

    public chessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
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
                return e.getMessage();
            }
        }
        throw new IllegalArgumentException("Expected: username password email");
    }

    public String login(String... params){
        if (params.length >= 2){
            try {
                auth = server.login(params[0], params[1]).authToken();
                isLoggedIn = true;
                return this.help();
            }catch (HttpRetryException e){
                return e.getMessage();
            }
        }
        throw new IllegalArgumentException("Expected: username password");
    }

    public String quit(){
        return "quit";
    }

    public String help(){
        if (isLoggedIn){
            return """
                    create <NAME> - make a game
                    list - show all games
                    join <ID> [WHITE|BLACK] - join a game a color
                    observe <ID> - observe a game
                    logout - exit the game
                    quit - to exit
                    help - see possible commands
                    """;
        } else {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to make an account
                    login <USERNAME> <PASSWORD> - to play
                    quit - to exit
                    help - see possible commands
                    """;
        }
    }

    public String create(String... params){
        assertSignedIn();
        if (params.length >= 1){
            try {
                server.create(auth,params[0]);
                return params[0] +" created";
            } catch (HttpRetryException e){
                return e.getMessage();
            }
        }
        throw new IllegalArgumentException("Expected: name");
    }

    public String list(){
        assertSignedIn();
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
            return e.getMessage();
        }
    }

    public String join(String... params){
        assertSignedIn();
        if (params.length >= 2){
            try {
                boolean isWhite = params[1] == "WHITE";
                GameData gameData = getRealGameID(params[0]);
                ChessGame game = gameData.game() != null ? gameData.game() : new ChessGame();
                DrawBoard drawBoard = new  DrawBoard(isWhite);
                drawBoard.drawBoard(game);
                server.join(auth, gameData.gameID(), params[1]);
                return "";
            } catch (Exception e){
                return e.getMessage();
            }
        }
        throw new IllegalArgumentException("Expected: ID# WHITE|BLACK");
    }

    public String observe(String... params){
        assertSignedIn();
        if (params.length >= 1){
            try {
                GameData game = getRealGameID(params[0]);
                DrawBoard showWhite = new DrawBoard(true);
                DrawBoard showBlack = new DrawBoard(false);
                showWhite.drawBoard(game.game());
                showBlack.drawBoard(game.game());
                return "";
            } catch (Exception e){
                return e.getMessage();
            }
        }
        throw new IllegalArgumentException("Expected: ID#");
    }

    public String logout(){
        assertSignedIn();
        try {
            server.logout(auth);
            auth = null;
            isLoggedIn = false;
            return "I guess you signed out";
        } catch (HttpRetryException e){
            return e.getMessage();
        }
    }

    private void assertSignedIn() throws IllegalAccessError{
        if(!isLoggedIn){
            throw new IllegalAccessError("You got to sign in first");
        }
    }

    private GameData getRealGameID(String fakeID){
        if (curGameList.isEmpty()){
            throw new IllegalArgumentException("Game does not exist");
        }
        return curGameList.get(Integer.parseInt(fakeID)-1);
    }
}
