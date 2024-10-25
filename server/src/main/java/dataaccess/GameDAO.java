package dataaccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    void clear();

    GameData getGame(int gameID) throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    HashSet<GameData> listGames();

    void updateGame(int gameID,GameData updatedGame) throws DataAccessException;

}
