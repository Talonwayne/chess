package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.nio.file.FileAlreadyExistsException;
import java.util.*;

public  class GameDAO {
    private HashSet<GameData> games;
    private int number;

    public GameDAO(){
        this.number = 1;
        this.games = new HashSet<>();
    }

    public  void clear(){
        games.clear();
    }

    public  GameData getGame(int gameID) throws InputMismatchException, DataAccessException {
        if (games == null || games.isEmpty()){
            throw new DataAccessException("List games is empty");
        }
        for (GameData game:games){
            if(game.gameID() == gameID){
                return game;
            }
        }
        throw new InputMismatchException("Game ID does not Exist");
    }

    public  int createGame(String gameName) throws FileAlreadyExistsException {
        for (GameData game:games){
            if(game.gameName().equals(gameName)){
                throw new FileAlreadyExistsException("GameName already exists");
            }
        }
        int gameID = number;
        number++;
        games.add(new GameData(gameID,"null","null",gameName,new ChessGame()));
        return gameID;
    }

    public  HashSet<GameData> listGames(){
        return games;
    }

    public  void updateGame(int gameID, GameData updatedGame) throws DataAccessException{
        GameData oldGame = getGame(gameID);
        games.remove(oldGame);
        games.add(updatedGame);
    }
}
