package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {


    protected ChessBoard board;
    protected ChessPosition position;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {

        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> getMoves() {

        return new ArrayList<>();
    }

    public static class KingMoves extends PieceMovesCalculator {
        public KingMoves(ChessBoard board, ChessPosition position) {
            super(board, position);

        }
        @Override
        public Collection<ChessMove> getMoves() {
            List<ChessMove> moves = new ArrayList<>();
            int[][] movementOptions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
            for (int[] xy : movementOptions) {
                int newrow = position.getRow() + xy[0];
                int newcol = position.getColumn() + xy[1];
                if (newrow >= 1 && newrow <= 8 && newcol >= 1 && newcol <= 8) {
                    ChessPosition newposition = new ChessPosition(newrow, newcol);
                    ChessPiece areyouempty = board.getPiece(newposition);
                    if (areyouempty == null || areyouempty.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        moves.add(new ChessMove(position, newposition, null));
                    }
                }
            }

            return moves;

        }
    }
}


/*
    public class QueenMoves extends PieceMovesCalculator {
        public QueenMoves(ChessBoard board, ChessPosition position) {

        }
    }

    public class KnightMoves extends PieceMovesCalculator {
        public KnightMoves(ChessBoard board, ChessPosition position) {

        }
    }

    public class BishopMoves extends PieceMovesCalculator {
        public BishopMoves(ChessBoard board, ChessPosition position) {

        }
    }

    public class RookMoves extends PieceMovesCalculator {
        public RookMoves(ChessBoard board, ChessPosition position) {

        }
    }

    public class PawnMoves extends PieceMovesCalculator {
        public PawnMoves(ChessBoard board, ChessPosition position) {

        }
    }
}

 */
