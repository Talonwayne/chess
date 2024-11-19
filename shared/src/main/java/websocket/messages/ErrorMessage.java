package websocket.messages;

public class ErrorMessage extends ServerMessage{
    public String message;
    ErrorMessage(String message){
        super(ServerMessageType.ERROR);
        this.message = message;
    }
}
