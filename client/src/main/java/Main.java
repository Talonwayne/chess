import chess.*;
import ui.DrawBoard;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String url = "http://localhost:8080";
        Repl repl = new Repl(url);
        repl.run();

    }
}