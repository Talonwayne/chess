package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    public ChessGame game;

    LoadGameMessage(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
