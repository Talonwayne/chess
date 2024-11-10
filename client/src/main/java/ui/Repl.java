package ui;

import java.util.Scanner;

public class Repl {
    private final chessClient client;

    public Repl(String serverUrl){
        client = new chessClient(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to 240 Chess by Talon Anderson. Type Help to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
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

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }


}
