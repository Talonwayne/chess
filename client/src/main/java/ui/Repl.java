package ui;

import java.util.Scanner;

public class Repl {
    private final chessClient client;

    public Repl(String serverUrl){
        client = new chessClient(serverUrl, this);
    }

    public void run(){
        System.out.println("Welcome to 240 Chess by Talon Anderson. Type Help to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String input = scanner.nextLine();
            try{
                result = client.eval(input);
            } catch (Exception e ){

            }

        }
    }


}
