package client;

import ui.EscapeSequences;
import websocket.NotificationHandler;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

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

    public void notify(NotificationMessage notification) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + notification.getMessage());
        printPrompt(client.isInGame,client.isInGame);
    }
}
