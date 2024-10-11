package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

public  class GameDAO {
    private List<GameData> games;
    private int number;

    public GameDAO(){
        this.number = 1;
        this.games = new ArrayList<>();
    }

    public  void clear(){
        games.clear();
    }

    public  GameData getGame(int gameID) throws DataAccessException{
        if (games == null || games.isEmpty()){
            throw new DataAccessException("List games is empty");
        }
        for (GameData game:games){
            if(game.gameID() == gameID){
                return game;
            }
        }
        throw new DataAccessException("Game ID does not Exist");
    }

    public  int createGame(String gameName) throws FileAlreadyExistsException, DataAccessException {
        for (GameData game:games){
            if(game.gameName().equals(gameName)){
                throw new FileAlreadyExistsException("GameName already exists");
            }
        }
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
