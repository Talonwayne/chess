package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;

import java.nio.file.FileAlreadyExistsException;
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
        if (color != "BLACK" && color != "WHITE") {
            throw new InputMismatchException("Color is not Black or White");
        }
        GameData desiredGame = gameDAO.getGame(gameID);
        GameData updatedGame;
        String whitePlayer = desiredGame.whiteUsername();
        String blackPlayer = desiredGame.blackUsername();
        String gameName = desiredGame.gameName();
        ChessGame game = desiredGame.game();
        if (color == "WHITE") {
            if (whitePlayer.equals("")) {
                updatedGame = new GameData(gameID, authDAO.getUsername(authToken), blackPlayer, gameName, game);
                gameDAO.updateGame(gameID, updatedGame);
            } else {
                throw new FileAlreadyExistsException("The White Pieces Are Taken");
            }
        } else if (color == "BLACK") {
            if (blackPlayer.equals("")) {
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

    public List<GameData> listGames(){
        return gameDAO.listGames();
    }


}
