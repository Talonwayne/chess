package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;
import java.util.InputMismatchException;

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

    public void joinGame(String authToken, String color, int gameID)
            throws FileAlreadyExistsException, InputMismatchException, DataAccessException, UnauthorisedException {
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

    public HashSet<GameData> listGames() throws DataAccessException{
        return gameDAO.listGames();
    }

    public AuthData register(String username, String password, String email) throws DataAccessException, UnauthorisedException {
        userDAO.createUser(username, password, email);
        return login(username, password);
    }
    public AuthData login(String username, String password) throws DataAccessException, UnauthorisedException {
        UserData user = userDAO.getUser(username);
        if (user.password().equals(password)) {
            return authDAO.createAuth(username);
        }
        throw new UnauthorisedException("Invalid username or password");
    }

    public void logout(String authorizationToken) throws DataAccessException {
        authDAO.deleteAuth(authorizationToken);
    }

    public boolean isValidAuth(String authToken) throws DataAccessException {
        return authDAO.validateAuth(authToken);
    }
}
