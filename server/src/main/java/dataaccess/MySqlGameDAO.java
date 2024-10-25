package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class MySqlGameDAO implements GameDAO{
    public void clear(){

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
