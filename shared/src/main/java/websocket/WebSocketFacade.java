package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.HttpRetryException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url) throws HttpRetryException{
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new HttpRetryException(ex.getMessage(),500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(String visitorName) throws HttpRetryException {
        try {
            UserGameCommand action = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HttpRetryException(ex.getMessage(),500);
        }
    }

    public void makeMove(String visitorName) throws HttpRetryException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, visitorName);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HttpRetryException(ex.getMessage(),500);
        }
    }

    public void leaveGame(String visitorName) throws HttpRetryException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, visitorName);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new HttpRetryException(ex.getMessage(),500);
        }
    }

    public void resignGame(String visitorName) throws HttpRetryException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, visitorName);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new HttpRetryException(ex.getMessage(),500);
        }
    }

}