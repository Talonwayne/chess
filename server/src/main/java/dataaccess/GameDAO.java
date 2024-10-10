package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public  class GameDAO {
    private List<GameData> games;

    public GameDAO(){
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

    public  void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        games.add(new GameData(gameID,whiteUsername,blackUsername,gameName,game));
    }

    public  List<GameData> listGames(){
        return games;
    }

    public  void updateGame(){

    }
}
