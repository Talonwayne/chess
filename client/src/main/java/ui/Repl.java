package ui;

public class Repl {
    private final chessClient client;

    public Repl(String serverUrl){
        client = new chessClient(serverUrl, this);
    }


}
