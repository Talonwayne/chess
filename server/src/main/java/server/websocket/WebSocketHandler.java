package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Service service;
    private static final Gson GSON = new Gson();

    public WebSocketHandler(Service service){
        this.service = service;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException{
        UserGameCommand command = GSON.fromJson(message, UserGameCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        String username = null;
        GameData gameData = null;

        try {

            username = service.getAuthDAO().getUsername(authToken);
            gameData = service.getGameDAO().getGame(gameID);

        }catch (DataAccessException e){
            connections.add(authToken,session,gameID);
            connections.whisper(authToken, new ErrorMessage("Error: Connection to the Game is Invalid"), gameID);
            connections.remove(authToken, gameID);
        }

        if (username == null) {
            connections.add(authToken,session,gameID);
            connections.whisper(authToken, new ErrorMessage("Error: Session Invalid"), gameID);
            connections.remove(authToken, gameID);
            return;
        } else if (gameData == null) {
            connections.add(authToken, session, gameID);
            connections.whisper(authToken, new ErrorMessage("Error: Connection to the Game is Invalid"), gameID);
            connections.remove(authToken,gameID);
            return;
        }

        ChessGame.TeamColor color;
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            color = ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            color = null;
        }

        switch (command.getCommandType()) {
            case CONNECT -> connect(authToken,gameID, session,gameData,username);
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = GSON.fromJson(message, MakeMoveCommand.class);
                makeMove(makeMoveCommand.getAuthToken(),makeMoveCommand.getGameID(), makeMoveCommand.getMove(), gameData, username, color);
            }
            case RESIGN -> resign(command.getAuthToken(),command.getGameID(), gameData, username);
            case LEAVE -> leave(command.getAuthToken(),command.getGameID(), gameData, username);
        }
    }

    private void connect(String authToken, int gameID, Session session, GameData gameData, String username) throws IOException{
        connections.add(authToken,session,gameID);
        LoadGameMessage lgm = new LoadGameMessage(gameData.game());
        connections.whisper(authToken, lgm,gameID);

        String message;
        if (username.equals(gameData.whiteUsername())){
            message = username + " has joined the game as white.";
        } else if (username.equals(gameData.blackUsername())) {
            message = username + " has joined the game as black.";
        } else {
            message = username + " is spectating this game.";
        }
        NotificationMessage nm = new NotificationMessage(message);
        connections.broadcast(authToken, nm,gameID);
    }

    private void makeMove(String authToken, int gameID, ChessMove move,
                          GameData gameData,String username ,ChessGame.TeamColor color) throws IOException{

        if (!gameData.game().curTeam.equals(color)){
            connections.whisper(authToken, new ErrorMessage("Error: Wow, you can't play for the other team!."),gameID);
            return;
        }

        if(gameData.game().isGameOver){
            connections.whisper(authToken, new ErrorMessage("Error: The game is over, no new moves can be made."),gameID);
            return;
        }

        try{
            gameData.game().makeMove(move);
        } catch (InvalidMoveException e) {
            connections.whisper(authToken, new ErrorMessage("Error: that is an Invalid Move."),gameID);
            return;
        }
        updateGame(authToken,gameData);

        connections.updateAllClientGames(new LoadGameMessage(gameData.game()),gameID);

        String message = username + " moved from " + move.getStartPosition() + " to " + move.getEndPosition() + ".";
        connections.broadcast(authToken,new NotificationMessage(message),gameID);

        ChessGame.TeamColor notCurPlayer = gameData.game().getTeamTurn() == ChessGame.TeamColor.BLACK
                ? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK;

        if(gameData.game().isInCheckmate(notCurPlayer)){
            String gameOverMessage = username + " has won the game by Checkmate!";
            connections.updateAllClientGames(new NotificationMessage(gameOverMessage),gameID);
            gameData.game().isGameOver = true;
            updateGame(authToken,gameData);
        } else if (gameData.game().isInStalemate(notCurPlayer)) {
            String gameOverMessage = "Game has ended in a stalemate!";
            connections.updateAllClientGames(new NotificationMessage(gameOverMessage),gameID);
            gameData.game().isGameOver = true;
            updateGame(authToken,gameData);
        } else if (gameData.game().isInCheck(notCurPlayer)) {
            String checkMessage = username + " has put the enemy king in Check!";
            connections.updateAllClientGames(new NotificationMessage(checkMessage),gameID);
        }
    }

    private void leave(String authToken, int gameID, GameData gameData,String username) throws IOException {

        String message = username + " has left the game.";
        connections.broadcast(authToken, new NotificationMessage(message),gameID);

        if (username.equals(gameData.blackUsername())) {
            gameData.setBlackUsername(null);
            updateGame(authToken, gameData);
        } else if (username.equals(gameData.whiteUsername())) {
            gameData.setWhiteUsername(null);
            updateGame(authToken, gameData);
        }
        connections.remove(authToken,gameID);
    }

    private void resign(String authToken, int gameID, GameData gameData, String username) throws IOException{

        if (gameData.game().isGameOver){
            connections.whisper(authToken, new ErrorMessage("Error: This game is already over."),gameID);
            return;
        }

        ChessGame.TeamColor color;
        if(gameData.whiteUsername().equals(username)){
            color = ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername().equals(username)) {
            color = ChessGame.TeamColor.BLACK;
        }else {
            color = null;
        }

        if(color == null){
            connections.whisper(authToken, new ErrorMessage("Error: Observers cannot resign the game."),gameID);
            return;
        }


        gameData.game().isGameOver = true;
        updateGame(authToken, gameData);

        NotificationMessage nm = new NotificationMessage(username + " has resigned the game!");
        connections.updateAllClientGames(nm,gameID);
    }

    private void updateGame(String authToken,GameData updatedData) throws IOException{
        try{
        service.getGameDAO().updateGame(updatedData.gameID(), updatedData);
        } catch (DataAccessException| NullPointerException e) {
            int gameID = updatedData.gameID();
            connections.whisper(authToken, new ErrorMessage("Error: Connection to the Game is Invalid"), gameID);
        }
    }
}