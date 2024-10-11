package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.nio.file.FileAlreadyExistsException;
import java.util.FormatFlagsConversionMismatchException;
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

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public void joinGame(String authToken, String color, int gameID) throws FileAlreadyExistsException,InputMismatchException, DataAccessException, UnauthorisedException{
        GameData desiredGame = gameDAO.getGame(gameID);
        String whitePlayer = desiredGame.whiteUsername();
        String blackPlayer = desiredGame.blackUsername();
        String gameName = desiredGame.gameName();
        ChessGame game = desiredGame.game();
        if (color.equals("WHITE")){
            if (whitePlayer.equals("")){
                GameData updatedGame = new GameData(gameID,authDAO.getUsername(authToken),blackPlayer,gameName,game) ;
            } else {
                throw new FileAlreadyExistsException("The White Pieces Are Taken");
            }
        } else if (color.equals("BLACK")) {
            if (blackPlayer.equals("")){
                GameData updatedGame = new GameData(gameID,whitePlayer,authDAO.getUsername(authToken),gameName,game) ;
            } else {
                throw new FileAlreadyExistsException("The Black Pieces Are Taken");
            }
        } else {
            throw new InputMismatchException("Color is not Black or White");
        }
        gameDAO.updateGame(gameID,desiredGame);
    }

    public int createGame(String gameName)throws FileAlreadyExistsException{
        return gameDAO.createGame(gameName);
    }

    public List<GameData> listGames(){
        return gameDAO.listGames();
    }


}
