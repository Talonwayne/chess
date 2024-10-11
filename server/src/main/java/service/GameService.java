package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;

import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;

public class GameService {
    public UserDAO userDAO;
    public AuthDAO authDAO;
    public GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void joinGame(String authToken, String color, int gameID) throws FileAlreadyExistsException,InputMismatchException, DataAccessException, UnauthorisedException {
        if (color == null || color.isEmpty()) {
            throw new InputMismatchException("Color is empty");
        }
        GameData desiredGame = gameDAO.getGame(gameID);
        GameData updatedGame;
        String whitePlayer = desiredGame.whiteUsername();
        String blackPlayer = desiredGame.blackUsername();
        String gameName = desiredGame.gameName();
        ChessGame game = desiredGame.game();
        if (color.equals("WHITE")) {
            if (whitePlayer.equals("null")) {
                updatedGame = new GameData(gameID, authDAO.getUsername(authToken), blackPlayer, gameName, game);
                gameDAO.updateGame(gameID, updatedGame);
            } else {
                throw new FileAlreadyExistsException("The White Pieces Are Taken");
            }
        } else if (color.equals("BLACK")) {
            if (blackPlayer.equals("null")) {
                updatedGame = new GameData(gameID, whitePlayer, authDAO.getUsername(authToken), gameName, game);
                gameDAO.updateGame(gameID, updatedGame);
            } else {
                throw new FileAlreadyExistsException("The Black Pieces Are Taken");
            }

        }
    }

    public int createGame(String gameName)throws FileAlreadyExistsException, DataAccessException{
        return gameDAO.createGame(gameName);
    }

    public HashSet<GameData> listGames(){
        return gameDAO.listGames();
    }


}
