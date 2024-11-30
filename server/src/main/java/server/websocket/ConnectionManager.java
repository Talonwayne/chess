package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, List<Connection>> gameConnections = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();

    public void add(String authToken, Session session, int gameID) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        gameConnections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(connection);
    }

    public void remove(String authToken, int gameID) {
        Connection connection = connections.remove(authToken);
        if (connection != null) {
            List<Connection> gameSessionList = gameConnections.get(gameID);
            if (gameSessionList != null) {
                gameSessionList.remove(connection);
                if (gameSessionList.isEmpty()) {
                    gameConnections.remove(gameID);  // Clean up if no sessions are left for this game
                }
            }
        }
    }

    public void broadcast(String excludeAuth, ServerMessage notification,int gameID) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        List<Connection> gameSessionList = gameConnections.get(gameID);
        if (gameSessionList != null) {
            for (var c : gameSessionList) {
                if (c.session.isOpen() && !c.authToken.equals(excludeAuth)) {
                    c.send(GSON.toJson(notification));
                }
            }
        }
        cleanUpConnections(removeList);
    }

    public void whisper(String authToken, ServerMessage notification, int gameID) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        for (var c : gameConnections.getOrDefault(gameID, new ArrayList<>())) {
            if (c.session.isOpen() && c.authToken.equals(authToken)) {
                c.send(GSON.toJson(notification));
            }
        }
        cleanUpConnections(removeList);
    }

   public void updateAllClientGames(ServerMessage update,int gameID) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        List<Connection> gameSessionList = gameConnections.get(gameID);
        if (gameSessionList != null) {
            for (var c : gameSessionList) {
                if (c.session.isOpen()) {
                    c.send(GSON.toJson(update));
                }
            }
        }
        cleanUpConnections(removeList);
    }

    private ArrayList<Connection> cleanUpInactiveConnections() {
        ArrayList<Connection> removeList = new ArrayList<>();
        for (var c : connections.values()) {
            if (!c.session.isOpen()) {
                removeList.add(c);
            }
        }
        return removeList;
    }

    private void cleanUpConnections(ArrayList<Connection> removeList) {
        for (var c : removeList) {
            connections.remove(c.authToken);
            for (var gameSessionList : gameConnections.values()) {
                gameSessionList.remove(c);
            }
        }
    }
}
