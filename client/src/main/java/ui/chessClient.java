package ui;

import java.util.Arrays;

public class chessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private boolean isLoggedIn = false;

    public chessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
                case "help" -> help();
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
        if (params.length == 3){

        }
        throw new IllegalArgumentException("Expected: username password email");
    }

    public String login(String... params){
        if (params.length == 2){

        }
        throw new IllegalArgumentException("Expected: username password");
    }

    public String quit(){}

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
        if (params.length == 3){

        }
        throw new IllegalArgumentException("Expected: username password email");
    }

    public String list(String... params){
        assertSignedIn();
        if (params.length == 3){

        }
        throw new IllegalArgumentException("Expected: username password email");
    }

    public String join(String... params){
        assertSignedIn();
        if (params.length == 3){

        }
        throw new IllegalArgumentException("Expected: username password email");
    }

    public String observe(String... params){
        assertSignedIn();
        if (params.length == 3){

        }
        throw new IllegalArgumentException("Expected: username password email");
    }

    public String logout(){
        assertSignedIn();
        isLoggedIn = false;
    }

    private void assertSignedIn() throws IllegalAccessError{
        if(!isLoggedIn){
            throw new IllegalAccessError("You got to sign in first");
        }
    }





}
