package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;

public class Service {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public Service(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;

    }

    public void clearAllData() {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    public void joinGame(String authToken, String color, int gameID) throws DataAccessException, FileAlreadyExistsException {
        if (color == null || color.isEmpty()) {
            throw new DataAccessException("Color is empty");
        }
        GameData desiredGame = gameDAO.getGame(gameID);
        if (desiredGame == null){
            throw new DataAccessException("Bad gameID");
        }
        GameData updatedGame;
        String whitePlayer = desiredGame.whiteUsername();
        String blackPlayer = desiredGame.blackUsername();
        String gameName = desiredGame.gameName();
        ChessGame game = desiredGame.game();
        if (color.equals("WHITE")) {
            if (whitePlayer == null) {
                updatedGame = new GameData(gameID, authDAO.getUsername(authToken), blackPlayer, gameName, game);
                gameDAO.updateGame(gameID, updatedGame);
            } else {
                throw new FileAlreadyExistsException("The White Pieces Are Taken");
            }
        } else if (color.equals("BLACK")) {
            if (blackPlayer == null) {
                updatedGame = new GameData(gameID, whitePlayer, authDAO.getUsername(authToken), gameName, game);
                gameDAO.updateGame(gameID, updatedGame);
            } else {
                throw new FileAlreadyExistsException("The Black Pieces Are Taken");
            }

        }
    }

    public int createGame(String gameName)throws DataAccessException{
        return gameDAO.createGame(gameName);
    }

    public HashSet<GameData> listGames() {
        return gameDAO.listGames();
    }

    public AuthData register(String username, String password, String email) throws DataAccessException {
        if (userDAO.getUser(username) != null){
            throw new DataAccessException("Duplicate Username");
        }
        userDAO.createUser(username,password,email);
        return login(username,password);
    }



    public AuthData login(String username, String password) throws DataAccessException {

        if (userDAO.verifyUser(username,password)) {
            return authDAO.createAuth(username);
        }
        throw new DataAccessException("Invalid username or password");
    }

    public void logout(String authorizationToken) throws DataAccessException {
        authDAO.deleteAuth(authorizationToken);
    }

    public boolean isValidAuth(String authToken) throws DataAccessException {
        return authDAO.validateAuth(authToken);
    }
}
