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


    public static class QueenMoves extends PieceMovesCalculator {
        public QueenMoves(ChessBoard board, ChessPosition position) {
            super(board, position);

        }
        private void findLines(List<ChessMove> moves, ChessPiece curPiece, int rowChange, int colChange){
            int curRow = position.getRow();
            int curCol = position.getColumn();

            for (int i = 1; i<=8; i++){
                int nRow = curRow+ i*rowChange;
                int nCol = curCol+ i*colChange;

                if (nRow >= 1 && nRow <= 8 && nCol >= 1 && nCol <= 8){
                    ChessPosition np = new ChessPosition(nRow,nCol);
                    ChessPiece areyouempty = board.getPiece(np);
                    if (areyouempty == null){
                        moves.add(new ChessMove(position,np,null));
                    } else if (areyouempty.getTeamColor() != curPiece.getTeamColor()){
                        moves.add(new ChessMove(position,np, null));
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        public Collection<ChessMove> getMoves() {
            List<ChessMove> moves = new ArrayList<>();
            //find the empty lines, put them in a list, then verify the movement options.
            ChessPiece curpiece = board.getPiece(position);
            if (curpiece==null){
                return moves;
            }
            int[][] movementOptions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
            for (int[] xy: movementOptions) {
                findLines(moves,curpiece,xy[0],xy[1]);

            }

            return moves;

        }
    }

    public static class KnightMoves extends PieceMovesCalculator {
        public KnightMoves(ChessBoard board, ChessPosition position) {
            super(board, position);

        }
        public Collection<ChessMove> getMoves() {
            List<ChessMove> moves = new ArrayList<>();
            int[][] movementOptions = {{-2, -1}, {-2, 1}, {-1, 2}, {-1, -2}, {1, 2}, {1, -2}, {2, 1}, {2, -1}};
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

    public static class BishopMoves extends PieceMovesCalculator {
        public BishopMoves(ChessBoard board, ChessPosition position) {
            super(board, position);

        }
        private void findLines(List<ChessMove> moves, ChessPiece curPiece, int rowChange, int colChange){
            int curRow = position.getRow();
            int curCol = position.getColumn();

            for (int i = 1; i<=8; i++){
                int nRow = curRow+ i*rowChange;
                int nCol = curCol+ i*colChange;

                if (nRow >= 1 && nRow <= 8 && nCol >= 1 && nCol <= 8){
                    ChessPosition np = new ChessPosition(nRow,nCol);
                    ChessPiece areyouempty = board.getPiece(np);
                    if (areyouempty == null){
                        moves.add(new ChessMove(position,np,null));
                    } else if (areyouempty.getTeamColor() != curPiece.getTeamColor()){
                        moves.add(new ChessMove(position,np, null));
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        public Collection<ChessMove> getMoves() {
            List<ChessMove> moves = new ArrayList<>();
            //find the empty lines, put them in a list, then verify the movement options.
            ChessPiece curpiece = board.getPiece(position);
            if (curpiece==null){
                return moves;
            }
            int[][] movementOptions = { {-1, 1}, {1, -1}, {1, 1}, {-1, -1}};
            for (int[] xy: movementOptions) {
                findLines(moves,curpiece,xy[0],xy[1]);

            }

            return moves;

        }
    }

    public static class RookMoves extends PieceMovesCalculator {
        public RookMoves(ChessBoard board, ChessPosition position) {
            super(board, position);

        }
        private void findLines(List<ChessMove> moves, ChessPiece curPiece, int rowChange, int colChange){
            int curRow = position.getRow();
            int curCol = position.getColumn();

            for (int i = 1; i<=8; i++){
                int nRow = curRow+ i*rowChange;
                int nCol = curCol+ i*colChange;

                if (nRow >= 1 && nRow <= 8 && nCol >= 1 && nCol <= 8){
                    ChessPosition np = new ChessPosition(nRow,nCol);
                    ChessPiece areyouempty = board.getPiece(np);
                    if (areyouempty == null){
                        moves.add(new ChessMove(position,np,null));
                    } else if (areyouempty.getTeamColor() != curPiece.getTeamColor()){
                        moves.add(new ChessMove(position,np, null));
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        public Collection<ChessMove> getMoves() {
            List<ChessMove> moves = new ArrayList<>();
            //find the empty lines, put them in a list, then verify the movement options.
            ChessPiece curpiece = board.getPiece(position);
            if (curpiece==null){
                return moves;
            }
            int[][] movementOptions = { {-1, 0}, {0, -1}, {0, 1}, {1, 0}};
            for (int[] xy: movementOptions) {
                findLines(moves,curpiece,xy[0],xy[1]);

            }

            return moves;

        }
    }

    public static class PawnMoves extends PieceMovesCalculator {
        public PawnMoves(ChessBoard board, ChessPosition position) {
            super(board, position);

        }
        public Collection<ChessMove> getMoves() {
            List<ChessMove> moves = new ArrayList<>();
            boolean promotion = false;
            //I have to know the color to do logic here
            if (board.getPiece(position).getTeamColor()== ChessGame.TeamColor.BLACK){
                //Autopromote?
                if (position.getRow()==2){
                    promotion = true;
                }
                //move forward 1
                ChessPosition np1 = new ChessPosition(position.getRow()-1, position.getColumn());
                ChessPiece areyouempty = board.getPiece(np1);
                if (areyouempty == null) {
                    if(promotion){
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(position, np1, null));
                    }
                }
                //move forward 2
                if (position.getRow()==7){
                    ChessPosition np2 = new ChessPosition(position.getRow()-2, position.getColumn());
                    ChessPiece areyouempty2 = board.getPiece(np2);
                    ChessPosition np5 = new ChessPosition(position.getRow()-1, position.getColumn());
                    ChessPiece areyouempty5 = board.getPiece(np5);
                    if (areyouempty2 == null && areyouempty5 == null) {
                        moves.add(new ChessMove(position,np2,null));
                    }
                }
                //captures
                ChessPosition np3 = new ChessPosition(position.getRow()-1, position.getColumn()+1);
                ChessPiece areyouempty3 = board.getPiece(np3);
                if (areyouempty3!=null) {
                    if (areyouempty3.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        if(promotion){
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(position, np3, null));
                        }
                    }
                }
                ChessPosition np4 = new ChessPosition(position.getRow()-1, position.getColumn()-1);
                ChessPiece areyouempty4 = board.getPiece(np4);
                if(areyouempty4!=null) {
                    if (areyouempty4.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        if(promotion){
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(position, np4, null));
                        }
                    }
                }
            } else{
                //WHITE
                if (position.getRow()==7){
                    promotion = true;
                }
                //Black move forward 1
                ChessPosition np1 = new ChessPosition(position.getRow()+1, position.getColumn());
                ChessPiece areyouempty = board.getPiece(np1);
                if (areyouempty == null) {
                    //Promotion? IDK how to do that yet
                    if(promotion){
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, np1, ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(position, np1, null));
                    }
                }
                //Black move forward 2
                if (position.getRow()==2){
                    ChessPosition np2 = new ChessPosition(position.getRow()+2, position.getColumn());
                    ChessPiece areyouempty2 = board.getPiece(np2);
                    ChessPosition np5 = new ChessPosition(position.getRow()+1, position.getColumn());
                    ChessPiece areyouempty5 = board.getPiece(np5);
                    if (areyouempty2 == null && areyouempty5 == null) {
                        moves.add(new ChessMove(position,np2,null));
                    }
                }
                //Captures
                ChessPosition np3 = new ChessPosition(position.getRow()+1, position.getColumn()+1);
                ChessPiece areyouempty3 = board.getPiece(np3);
                if (areyouempty3!=null) {
                    if (areyouempty3.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if(promotion){
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(position, np3, ChessPiece.PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(position, np3, null));
                        }
                    }
                }
                ChessPosition np4 = new ChessPosition(position.getRow()+1, position.getColumn()-1);
                ChessPiece areyouempty4 = board.getPiece(np4);
                if(areyouempty4!=null) {
                    if (areyouempty4.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if(promotion){
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(position, np4, ChessPiece.PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(position, np4, null));
                        }
                    }
                }
            }
            return moves;

        }
    }
}
