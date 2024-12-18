package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType type;
    private boolean enpassant = false;
    private boolean castle = false;

    public boolean isEnpassant() {
        return enpassant;
    }

    public void setEnpassant(boolean yes){
        enpassant = yes;
    }
    public void setCastle(boolean yes){
        castle = yes;
    }
    public boolean isCastle(){return castle;}


    @Override
    public boolean equals(Object o) {
        if (this == o){return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(start, chessMove.start) && Objects.equals(end, chessMove.end) && type == chessMove.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, type);
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.end = endPosition;
        this.start = startPosition;
        this.type = promotionPiece;

    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    @Override
    public String toString() {
        return "ChessMove{\n" +
                "start=" + start +
                ",\nend=" + end +
                ",\ntype=" + type +
                '}';
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return type;
    }

}
