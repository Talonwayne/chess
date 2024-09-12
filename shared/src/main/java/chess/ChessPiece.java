package chess;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {

        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return color;

    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        PieceMovesCalculator calc = new PieceMovesCalculator(board, myPosition);
        switch (type) {
            case KING:
                calc = PieceMovesCalculator.KingMoves(board, myPosition);
                break;
            case QUEEN:
                calc = new PieceMovesCalculator().QueenMoves(board, myPosition);
                break;
            case BISHOP:
                calc = new PieceMovesCalculator().BishopMoves(board, myPosition);
                break;
            case KNIGHT:
                calc = new PieceMovesCalculator().KnightMoves(board, myPosition);
                break;
            case ROOK:
                calc = new PieceMovesCalculator().RookMoves(board, myPosition);
                break;
            case PAWN:
                calc = new PieceMovesCalculator().PawnMoves(board, myPosition);
                break;
            default:
                throw new Exception("bad piece type");
        }

        return calc;

    }
}
