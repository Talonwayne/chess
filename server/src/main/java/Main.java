import chess.*;
import dataaccess.*;
import server.Server;
import service.Service;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        if(args.length >= 2 && args[1].equals("sql")) {
            userDAO = new MySqlUserDAO();
            gameDAO = new MySqlGameDAO();
            authDAO = new MySqlAuthDAO();
        }
        Service theService = new Service(userDAO,gameDAO,authDAO);
        Server s = new Server();
        s.setService(theService);
        s.run(8080);
    }
}