package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MovementCalc{
    protected ChessBoard board;
    protected ChessPosition position;
    protected ChessPiece piece;
    MovementCalc(ChessBoard board,ChessPosition position){
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }
    public Collection<ChessMove> calcMoves(){
        return new ArrayList<>();
    }
    public void findLines(List<ChessMove> moves, int changeRow, int changeCol){
        for (int i = 1; i <= 8; i++){
            int curRow = position.getRow()+i*changeRow;
            int curCol = position.getColumn()+i*changeCol;
            ChessPosition newPosition = new ChessPosition(curRow, curCol);
            if(isValidPosition(newPosition)){
                if(isEmpty(newPosition)){
                    moves.add(new ChessMove(position,newPosition,null));
                } else if (isEnemy(newPosition)) {
                    moves.add(new ChessMove(position,newPosition,null));
                    break;
                } else {
                    break;
                }
            }
        }
    }
    public void findMoves(List<ChessMove> moves, int[][] direction ){
        for(int[] xy:direction){
            int nRow = position.getRow()+xy[0];
            int nCol = position.getColumn()+xy[1];
            ChessPosition newPosition = new ChessPosition(nRow,nCol);
            if(isValidPosition(newPosition)){
                if(isEmpty(newPosition)||isEnemy(newPosition)){
                    moves.add(new ChessMove(position,newPosition, null));
                }
            }
        }
    }
    public boolean isValidPosition(ChessPosition newPosition){
        return newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8 && newPosition.getRow() >= 1 && newPosition.getRow() <= 8;
    }
    public boolean isEmpty(ChessPosition pos){
        ChessPiece nPiece = board.getPiece(pos);
        return nPiece == null;
    }
    public boolean isEnemy(ChessPosition newPosition){
        if(isEmpty(newPosition)){
            return false;
        }
        ChessPiece nPiece = board.getPiece(newPosition);
        return nPiece.getTeamColor() != piece.getTeamColor();
    }

    public static class KingMoves extends MovementCalc{
        KingMoves(ChessBoard board,ChessPosition position){
            super(board,position);
        }
        @Override
        public Collection<ChessMove> calcMoves(){
            int[][] directions = {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
            List<ChessMove> moves = new ArrayList<>();
            findMoves(moves,directions);
            return moves;
        }
    }
    public static class KnightMoves extends MovementCalc{
        KnightMoves(ChessBoard board,ChessPosition position){
            super(board,position);
        }
        @Override
        public Collection<ChessMove> calcMoves(){
            int[][] directions = {{1,2},{1,-2},{-1,-2},{-1,2},{2,-1},{2,1},{-2,1},{-2,-1}};
            List<ChessMove> moves = new ArrayList<>();
            findMoves(moves,directions);
            return moves;
        }
    }
    public static class QueenMoves extends MovementCalc{
        QueenMoves(ChessBoard board,ChessPosition position){
            super(board,position);
        }
        @Override
        public Collection<ChessMove> calcMoves() {
            int[][] directions = {{1, 1}, {1, 0}, {1, -1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
            List<ChessMove> moves = new ArrayList<>();
            for(int[] xy: directions){
                findLines(moves,xy[0],xy[1]);
            }
            return moves;
        }
    }
    public static class RookMoves extends MovementCalc{
        RookMoves(ChessBoard board,ChessPosition position){
            super(board,position);
        }
        @Override
        public Collection<ChessMove> calcMoves() {
            int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
            List<ChessMove> moves = new ArrayList<>();
            for(int[] xy: directions){
                findLines(moves,xy[0],xy[1]);
            }
            return moves;
        }
    }
    public static class BishopMoves extends MovementCalc{
        BishopMoves(ChessBoard board,ChessPosition position){
            super(board,position);
        }
        @Override
        public Collection<ChessMove> calcMoves() {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            List<ChessMove> moves = new ArrayList<>();
            for(int[] xy: directions){
                findLines(moves,xy[0],xy[1]);
            }
            return moves;
        }
    }
    public static class PawnMoves extends MovementCalc{
        PawnMoves(ChessBoard board,ChessPosition position){
            super(board,position);
        }
        public void promotions(List<ChessMove> moves, ChessPosition newPosition){
            boolean blackPromote = (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1);
            boolean whitePromote = (piece.getTeamColor()== ChessGame.TeamColor.WHITE && newPosition.getRow()==8);
            if (blackPromote || whitePromote) {
                moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
            }else{
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        @Override
        public Collection<ChessMove> calcMoves(){
            List<ChessMove> moves = new ArrayList<>();
            int orientation = 1;
            if(piece.getTeamColor()== ChessGame.TeamColor.BLACK){
                orientation = -1;
            }
            //single move
            int nRow = position.getRow()+orientation;
            ChessPosition newPosition = new ChessPosition(nRow, position.getColumn());
            if(isValidPosition(newPosition) && isEmpty(newPosition)){
                promotions(moves,newPosition);
                //double move
                boolean blackFirstMove = (piece.getTeamColor() == ChessGame.TeamColor.BLACK && position.getRow() == 7);
                boolean whiteFirstMove = (piece.getTeamColor()== ChessGame.TeamColor.WHITE && position.getRow()==2);
                if(blackFirstMove || whiteFirstMove) {
                    int dRow = position.getRow() + orientation * 2;
                    newPosition = new ChessPosition(dRow, position.getColumn());
                    if (isValidPosition(newPosition) && isEmpty(newPosition)){
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }
            //captures
            for (int i = -1;i<=1;i+=2) {
                int nCol = position.getColumn() + i;
                newPosition = new ChessPosition(nRow, nCol);
                if (isValidPosition(newPosition)) {
                    if (isEnemy(newPosition)) {
                        promotions(moves, newPosition);
                    }
                }
            }
            return moves;
        }
    }
}
