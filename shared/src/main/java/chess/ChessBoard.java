package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{

    private ChessPiece[][] squares = new ChessPiece[10][10];

    public ChessBoard() {
        
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }
    @Override
    public ChessBoard clone() {
        try {
            ChessBoard copy = (ChessBoard) super.clone();
            copy.squares = new ChessPiece[this.squares.length][];
            for (int i = 0; i < this.squares.length; i++) {
                copy.squares[i] = Arrays.copyOf(this.squares[i], this.squares[i].length);
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChessBoard:\n");
        for (int row = 8; row >= 1; row--) { // Loop from top to bottom
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = squares[row][col];
                if (piece == null) {
                    sb.append(". ");
                } else {
                    char symbol = getPieceSymbol(piece);
                    sb.append(symbol).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    private char getPieceSymbol(ChessPiece piece) {
        char symbol;
        switch (piece.getPieceType()) {
            case KING:
                symbol = 'K';
                break;
            case QUEEN:
                symbol = 'Q';
                break;
            case ROOK:
                symbol = 'R';
                break;
            case BISHOP:
                symbol = 'B';
                break;
            case KNIGHT:
                symbol = 'N';
                break;
            case PAWN:
                symbol = 'P';
                break;
            default:
                symbol = '?';
                break;
        }
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? Character.toLowerCase(symbol) : symbol;
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public ChessPosition findKing(ChessGame.TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = squares[row][col];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return null;
    }

    public Collection<ChessPosition> findEnemies(ChessGame.TeamColor myTeam){
        Collection<ChessPosition> pieces = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if(squares[row][col]!= null){
                    if(squares[row][col].getTeamColor() != myTeam){
                        pieces.add(new ChessPosition(row,col));
                    }
                }
            }
        }
        return pieces;
    }
    public void resetBoard() {
//row 1
        squares[1][1] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK);
        squares[1][2] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT);
        squares[1][3] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP);
        squares[1][4] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.QUEEN);
        squares[1][5] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KING);
        squares[1][6] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP);
        squares[1][7] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT);
        squares[1][8] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK);
        // row 2
        squares[2][1] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        squares[2][2] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        squares[2][3] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        squares[2][4] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        squares[2][5] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        squares[2][6] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        squares[2][7] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        squares[2][8] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);
        //row 3,4,5,6 are empty
        //row 7
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        squares[7][8] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        //row 8
        squares[8][1] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK);
        squares[8][2] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT);
        squares[8][3] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP);
        squares[8][4] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.QUEEN);
        squares[8][5] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KING);
        squares[8][6] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP);
        squares[8][7] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT);
        squares[8][8] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK);
    }
}
