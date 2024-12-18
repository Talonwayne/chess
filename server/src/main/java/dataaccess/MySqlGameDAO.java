package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.HashSet;

public class MySqlGameDAO implements GameDAO{
    MySqlHelper helper;

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games(
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256),
              `ChessGameJson` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    public MySqlGameDAO(){
        try {
            helper = new MySqlHelper(createStatements);
        }catch (Exception e){
            System.out.println("Helper failed to build");
        }
    }

    public void clear(){
        var statement = "TRUNCATE games";
        try {
            helper.executeUpdate(statement);
        } catch (DataAccessException e){
            System.out.println("Failed to Clear DB");
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
                    MySqlHelper.fromJson(resultSet.getString("ChessGameJson"), ChessGame.class)
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
        HashSet<GameData> games = listGames();
        for (GameData game: games){
            if (game.gameName().equals(gameName)){
                throw new DataAccessException("gameName Already Exists");
            }
        }
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, ChessGameJson) VALUES (?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        try {
            return helper.executeUpdate(statement, null, null, gameName, MySqlHelper.toJson(game));
        } catch (DataAccessException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    public HashSet<GameData> listGames() throws DataAccessException{
        var statement = "SELECT * FROM games";
        HashSet<GameData> games = new HashSet<>();
        try {
            var resultSet = helper.executeQuery(statement);
            while (resultSet.next()) {
                games.add(new GameData(
                    resultSet.getInt("gameID"),
                    resultSet.getString("whiteUsername"),
                    resultSet.getString("blackUsername"),
                    resultSet.getString("gameName"),
                    MySqlHelper.fromJson(resultSet.getString("ChessGameJson"), ChessGame.class)
                ));
            }
        } catch (DataAccessException| SQLException e) {
            throw new DataAccessException("List Games Failed");
        }
        return games;
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, ChessGameJson = ? WHERE gameID = ?";
        try {
            helper.executeUpdate(statement, updatedGame.whiteUsername(),
                    updatedGame.blackUsername(), updatedGame.gameName(),
                    MySqlHelper.toJson(updatedGame.game()), gameID);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }
}
