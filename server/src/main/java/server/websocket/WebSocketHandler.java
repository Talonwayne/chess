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
    private final Gson GSON = new Gson();

    public WebSocketHandler(Service service){
        this.service = service;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException{
        UserGameCommand command = GSON.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(),command.getGameID(), session);
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = GSON.fromJson(message, MakeMoveCommand.class);
                makeMove(makeMoveCommand.getAuthToken(),makeMoveCommand.getGameID(), makeMoveCommand.getMove());
            }
            case RESIGN -> resign(command.getAuthToken(),command.getGameID());
            case LEAVE -> leave(command.getAuthToken(),command.getGameID());
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException{
        try {
            String username = service.getAuthDAO().getUsername(authToken);
            GameData gameData = service.getGameDAO().getGame(gameID);

            connections.add(authToken, session);
            if (username == null) {
                connections.whisper(authToken, new ErrorMessage("Error: Connection to the user is Invalid"));
                connections.remove(authToken);
                return;
            } else if (gameData == null) {
                connections.whisper(authToken,new ErrorMessage("Error: Connection to the game is Invalid"));
                connections.remove(authToken);
                return;
            }


        LoadGameMessage lgm = new LoadGameMessage(gameData.game());
        connections.whisper(authToken, lgm);

        String message;
        if (username.equals(gameData.whiteUsername())){
            message = username + " has joined the game as white.";
        } else if (username.equals(gameData.blackUsername())) {
            message = username + " has joined the game as black.";
        } else {
            message = username + " is spectating this game.";
        }
        NotificationMessage nm = new NotificationMessage(message);
        connections.broadcast(authToken, nm);
        } catch (DataAccessException e) {
            connections.whisper(authToken, new ErrorMessage("Error: Session Invalid"));
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move) throws IOException{
        try {
            String username = service.getAuthDAO().getUsername(authToken);
            GameData gameData = service.getGameDAO().getGame(gameID);

            if (username == null){
                connections.whisper(authToken, new ErrorMessage("Error: Session Invalid"));
                return;
            } else if (gameData == null) {
                connections.whisper(authToken, new ErrorMessage("Error: Connection to the Game is Invalid"));
                return;
            }

            if(gameData.game().isGameOver){
                connections.whisper(authToken, new ErrorMessage("Error: The game is over, no new moves can be made."));
                return;
            }

            try{
                gameData.game().makeMove(move);
            } catch (InvalidMoveException e) {
                connections.whisper(authToken, new ErrorMessage("Error: that is an Invalid Move."));
                return;
            }
            updateGame(authToken,gameData);

            connections.updateAllClientGames(new LoadGameMessage(gameData.game()));

            String message = username + " moved from " + move.getStartPosition() + " to " + move.getEndPosition() + ".";
            connections.broadcast(authToken,new NotificationMessage(message));

            ChessGame.TeamColor notCurPlayer = gameData.game().getTeamTurn() == ChessGame.TeamColor.BLACK ? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK;

            if(gameData.game().isInCheckmate(notCurPlayer)){
                String gameOverMessage = username + " has won the game by Checkmate!";
                connections.updateAllClientGames(new NotificationMessage(gameOverMessage));
                gameData.game().isGameOver = true;
                updateGame(authToken,gameData);
            } else if (gameData.game().isInStalemate(notCurPlayer)) {
                String gameOverMessage = "Game has ended in a stalemate!";
                connections.updateAllClientGames(new NotificationMessage(gameOverMessage));
                gameData.game().isGameOver = true;
                updateGame(authToken,gameData);
            } else if (gameData.game().isInCheck(notCurPlayer)) {
                String checkMessage = username + " has put the enemy king in Check!";
                connections.updateAllClientGames(new NotificationMessage(checkMessage));
            }
        } catch (Exception e) {
            connections.whisper(authToken, new ErrorMessage("Error: Websocket"));
        }
    }

    private void leave(String authToken, int gameID) throws IOException {
        try {
            String username = service.getAuthDAO().getUsername(authToken);
            GameData gameData = service.getGameDAO().getGame(gameID);

            if (username == null) {
                connections.whisper(authToken, new ErrorMessage("Error: Session Invalid"));
                return;
            } else if (gameData == null) {
                connections.whisper(authToken, new ErrorMessage("Error: Connection to the Game is Invalid"));
                return;
            }

            String message = username + " has left the game.";
            connections.broadcast(authToken, new NotificationMessage(message));

            if (username.equals(gameData.blackUsername())) {
                gameData.setBlackUsername(null);
                updateGame(authToken, gameData);
            } else if (username.equals(gameData.whiteUsername())) {
                gameData.setWhiteUsername(null);
                updateGame(authToken, gameData);
            }

            connections.remove(authToken);
            } catch (Exception e) {
                connections.whisper(authToken, new ErrorMessage("Error: Websocket"));
            }
    }

    private void resign(String authToken, int gameID) throws IOException{
        try {
            String username = service.getAuthDAO().getUsername(authToken);
            GameData gameData = service.getGameDAO().getGame(gameID);

            if (username == null) {
                connections.whisper(authToken, new ErrorMessage("Error: Session Invalid"));
                return;
            } else if (gameData == null) {
                connections.whisper(authToken, new ErrorMessage("Error: Connection to the Game is Invalid"));
                return;
            }

            gameData.game().isGameOver = true;
            updateGame(authToken, gameData);

            NotificationMessage nm = new NotificationMessage(username + " has resigned the game!");
            connections.updateAllClientGames(nm);
        } catch (Exception e) {
            connections.whisper(authToken, new ErrorMessage("Error: Websocket"));
        }
    }

    private void updateGame(String authToken,GameData updatedData) throws IOException{
        try{
        service.getGameDAO().updateGame(updatedData.gameID(), updatedData);
        } catch (DataAccessException| NullPointerException e) {
            connections.whisper(authToken, new ErrorMessage("Error: Connection to the Game is Invalid"));
        }
    }
}