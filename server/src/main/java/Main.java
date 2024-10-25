import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        boolean sql = false;
        if (args.length >= 2 && args[1].equals("sql")) {
            sql = true;
        }
        Server s = new Server();
        s.setDataAccess(sql);
        s.run(8080);
    }
}