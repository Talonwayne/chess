package client;

import com.google.gson.Gson;
import ui.EscapeSequences;
import websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;
    private static final Gson GSON = new Gson();

    public Repl(String serverUrl){
        client = new ChessClient(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to 240 Chess by Talon Anderson. Here are the possible commands" + EscapeSequences.SET_TEXT_COLOR_GREEN);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        System.out.print(client.evaluate(""));
        while (!result.equals(EscapeSequences.SET_TEXT_COLOR_YELLOW + "Thanks for Playing")){
            printPrompt(client.isInGame, client.isLoggedIn);
            String input = scanner.nextLine();
            try{
                result = client.evaluate(input);
                System.out.print(result);
            } catch (Exception e ){
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }

    private void printPrompt(Boolean isInGame,Boolean isLoggedIn) {
        if (isInGame){
            System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[IN GAME] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
        } else if (isLoggedIn){
            System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[LOGGED IN] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[LOGGED OUT] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
        }
    }

    public void notify(String message) {
            ServerMessage serverMessage = GSON.fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage loadGameMessage = GSON.fromJson(message, LoadGameMessage.class);
                    client.setCurGame(loadGameMessage.getGame());
                    client.redrawBoard();
                }
                case ERROR -> {
                    ErrorMessage errorMessage = GSON.fromJson(message, ErrorMessage.class);
                    System.out.print(errorMessage.getMessage());
                    printPrompt(client.isInGame,client.isLoggedIn);
                }
                case NOTIFICATION -> {
                    NotificationMessage notificationMessage = GSON.fromJson(message, NotificationMessage.class);
                    System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW + notificationMessage.getMessage());
                }
            }
    }
}
