import chess.*;
import ui.DrawBoard;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessGame game = new ChessGame();
        DrawBoard whiteDisplay = new DrawBoard(true);
        DrawBoard blackDisplay = new DrawBoard(false);
        whiteDisplay.drawBoard(game);
        blackDisplay.drawBoard(game);
    }
}