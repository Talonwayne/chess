package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(),command.getGameID(), session);
            case MAKE_MOVE -> makeMove(command.getAuthToken(),command.getGameID(), session);
            case RESIGN -> resign(command.getAuthToken(),command.getGameID(), session);
            case LEAVE -> leave(command.getAuthToken(),command.getGameID(), session);
        }
    }

    private void connect(String authToken, int GameID, Session session) throws IOException {
        connections.add(authToken, session);
        String message = String.format("%s is in the shop", visitorName);
        NotificationMessage nm = new NotificationMessage(message);
        connections.broadcast(authToken, NotificationMessage);
    }

    private void makeMove(String authToken, int GameID, Session session) throws IOException {
        connections.add(authToken, session);
        var message = String.format("%s is in the shop", visitorName);
        var NotificationMessage = new NotificationMessage(NotificationMessage.Type.ARRIVAL, message);
        connections.broadcast(authToken, NotificationMessage);
    }

    private void resign(String authToken, int GameID, Session session) throws IOException {
        connections.add(authToken, session);
        String message = String.format("%s is in the shop", visitorName);
        NotificationMessage notificationMessage = new NotificationMessage();
        connections.broadcast(authToken, notificationMessage);
    }

    private void leave(String authToken) throws IOException {
        connections.remove(authToken);
        var message = String.format("%s left the shop", visitorName);
        var NotificationMessage = new NotificationMessage(NotificationMessage.Type.DEPARTURE, message);
        connections.broadcast(authToken, NotificationMessage);
    }
}