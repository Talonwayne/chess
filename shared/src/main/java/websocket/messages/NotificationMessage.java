package websocket.messages;

public class NotificationMessage extends ServerMessage{
    public String message;

    NotificationMessage(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
