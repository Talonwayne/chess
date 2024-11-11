package ui;

import java.util.Scanner;

public class Repl {
    private final chessClient client;

    public Repl(String serverUrl){
        client = new chessClient(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to 240 Chess by Talon Anderson. Here are the possible commands" + EscapeSequences.SET_TEXT_COLOR_GREEN);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        System.out.print(client.evaluate(""));
        while (!result.equals("quit")){
            printPrompt(client.isLoggedIn);
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

    private void printPrompt(Boolean isLoggedIn) {
        if (isLoggedIn) {
            System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[LOGGED IN] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[LOGGED OUT] >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
        }
    }


}
