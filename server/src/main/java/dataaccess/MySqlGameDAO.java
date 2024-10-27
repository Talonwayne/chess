package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.HashSet;

public class MySqlGameDAO implements GameDAO{
    MySqlHelper helper;

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256),
              `ChessGameJson` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(gameID),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName),
              INDEX(ChessGameJson)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySqlGameDAO(){
        try {
            helper = new MySqlHelper(createStatements);
            helper.configureDatabase();
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

    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE gameID = ?";
        try {
            var resultSet = helper.executeQuery(statement, gameID);
            if (resultSet.next()) {
                return new GameData(
                    resultSet.getInt("gameID"),
                    resultSet.getString("whiteUsername"),
                    resultSet.getString("blackUsername"),
                    resultSet.getString("gameName"),
                    helper.fromJson(resultSet.getString("ChessGameJson"), ChessGame.class)
                );
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        } catch (SQLException e) {
            throw new DataAccessException("Error of SQL " + e.getMessage());
        }
        return null;
    }

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameName) VALUES (?)";
        try {
            return helper.executeUpdate(statement, gameName); 
        } catch (DataAccessException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    public HashSet<GameData> listGames() {
        var statement = "SELECT * FROM games";
        HashSet<GameData> games = new HashSet<>();
        try {
            var resultSet = helper.executeQuery(statement);
            while (resultSet.next()) {
                ChessGame chessGame = new ChessGame(); 
                games.add(new GameData(
                    resultSet.getInt("gameID"),
                    resultSet.getString("whiteUsername"),
                    resultSet.getString("blackUsername"),
                    resultSet.getString("gameName"),
                    helper.fromJson(resultSet.getString("ChessGameJson"), ChessGame.class)
                ));
            }
        } catch (DataAccessException e) {
            int i = 1;
        } catch (SQLException e) {
            int i = 1;
        }
        return games;
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ? WHERE gameID = ?";
        try {
            helper.executeUpdate(statement, updatedGame.whiteUsername(), updatedGame.blackUsername(), updatedGame.gameName(), gameID);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }
}
