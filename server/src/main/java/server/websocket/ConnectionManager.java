package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

public class ConnectionManager {
    // Map to store all active connections by authToken
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    // Map to track which session is associated with which gameID
    private final ConcurrentHashMap<Integer, List<Connection>> gameConnections = new ConcurrentHashMap<>();
    private final Gson GSON = new Gson();

    // Add a connection to a game
    public void add(String authToken, Session session, int gameID) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);

        // Track the connection by gameID
        gameConnections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(connection);
    }

    // Remove a connection from the game
    public void remove(String authToken, int gameID) {
        // Remove from both game-specific and global maps
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

    // Broadcast a notification to all players in the same game, excluding one
    public void broadcast(String excludeAuth, ServerMessage notification,int gameID) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        // Get all connections for the game
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

    // Send a whisper (private message) to a specific user in a game
    public void whisper(String authToken, ServerMessage notification, int gameID) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        // Find the connection by authToken and send the message if session is open
        for (var c : gameConnections.getOrDefault(gameID, new ArrayList<>())) {
            if (c.session.isOpen() && c.authToken.equals(authToken)) {
                c.send(GSON.toJson(notification));
            }
        }

        cleanUpConnections(removeList);
    }

    // Update all clients in the game (e.g., after a move, game update)
    public void updateAllClientGames(ServerMessage update,int gameID) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        // Get all connections for the game
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

    // Clean up inactive connections
    private ArrayList<Connection> cleanUpInactiveConnections() {
        ArrayList<Connection> removeList = new ArrayList<>();
        for (var c : connections.values()) {
            if (!c.session.isOpen()) {
                removeList.add(c);
            }
        }
        return removeList;
    }

    // Remove inactive connections from the game and global maps
    private void cleanUpConnections(ArrayList<Connection> removeList) {
        for (var c : removeList) {
            connections.remove(c.authToken);  // Remove from global map
            // Also remove from any associated game-specific list
            for (var gameSessionList : gameConnections.values()) {
                gameSessionList.remove(c);
            }
        }
    }
}
