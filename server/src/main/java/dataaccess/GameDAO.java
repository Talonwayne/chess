package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public  class GameDAO {
    private List<GameData> games;
    private int number;

    public GameDAO(){
        number = 1;
    }

    public  void clear(){
        games.clear();
    }

    public  GameData getGame(int gameID) throws DataAccessException{
        for (GameData game:games){
            if(game.gameID() == gameID){
                return game;
            }
        }
        throw new DataAccessException("Game ID does not Exist");
    }

    public  int createGame(String gameName){
        int gameID = number;
        number++;
        games.add(new GameData(gameID,"","",gameName,new ChessGame()));
        return gameID;
    }

    public  List<GameData> listGames(){
        return games;
    }

    public  void updateGame(int gameID, GameData updatedGame) throws DataAccessException{
        GameData oldGame = getGame(gameID);
        games.remove(oldGame);
        games.add(updatedGame);
    }
}
