package websocket.commands;

import chess.ChessMove;

public class makeMoveCommand extends UserGameCommand{
    private final ChessMove move;

    public makeMoveCommand(String at,int gameID, ChessMove move){
        super(CommandType.MAKE_MOVE, at, gameID);
        this.move = move;
    }
}
