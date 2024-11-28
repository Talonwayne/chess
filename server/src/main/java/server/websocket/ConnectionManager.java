package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson GSON = new Gson();

    public void add(String authToken, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeAuth, ServerMessage notification) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        for (var c : connections.values()) {
            if (c.session.isOpen() && !c.authToken.equals(excludeAuth)) {
                c.send(GSON.toJson(notification));
            }
        }

        // Clean up any connections that were left open
        cleanUpConnections(removeList);
    }

    public void whisper(String authToken, ServerMessage notification) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        for (var c : connections.values()) {
            if (c.session.isOpen() && c.authToken.equals(authToken)) {
                c.send(GSON.toJson(notification));
            }
        }
        cleanUpConnections(removeList);
    }

    public void updateAllClientGames(ServerMessage update) throws IOException {
        ArrayList<Connection> removeList = cleanUpInactiveConnections();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                c.send(GSON.toJson(update));
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
        }
    }
}