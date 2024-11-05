package ui;

public class chessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private boolean isLoggedIn = false;

    public chessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }


}
