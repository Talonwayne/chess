package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.HashSet;

public class MySqlGameDAO implements GameDAO{
    MySqlHelper helper;

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              'gameID' int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              'gameName' varchar(256),
              'ChessGameJson' TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(gameID),
              INDEX(whiteUsername)
              INDEX(blackUsername)
              INDEX(gameName)
              INDEX(ChessGameJson)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySqlGameDAO(){
        try {
            helper = new MySqlHelper(createStatements);
        }catch (Exception e){
            int i = 1;
        }
    }

    public void clear(){
        var statement = "TRUNCATE games";
        try {
            helper.executeUpdate(statement);
        } catch (DataAccessException e){
            int i = 1;
        }
    }

    public GameData getGame(int gameID) throws DataAccessException{
        ChessGame c = new ChessGame();
        return new GameData(123,"","","", c);
    }

    public int createGame(String gameName) throws DataAccessException{
        return 1232;

    }

    public HashSet<GameData> listGames() {
        return new HashSet<GameData>();
    }

    public void updateGame(int gameID,GameData updatedGame) throws DataAccessException{

    }


}