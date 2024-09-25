package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable{

    private TeamColor curTeam;
    private ChessBoard curBoard;
    public boolean whiteCastleQueenside = true;
    public boolean whiteCastleKingside = true;
    public boolean blackCastleQueenside = true;
    public boolean blackCastleKingside = true;
    public ChessMove enpassantable;


    public ChessGame() {
        this.curBoard = new ChessBoard();
        curBoard.resetBoard();
        curTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return curTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        curTeam = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return curTeam == chessGame.curTeam && Objects.equals(curBoard, chessGame.curBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(curTeam, curBoard);
    }
    @Override
    public ChessGame clone() {
        try {
            ChessGame copy = (ChessGame) super.clone();
            // Shallow clone of the board
            copy.curBoard = this.curBoard.clone(); // This performs the shallow copy
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported");
        }
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = curBoard.getPiece(startPosition);
        Collection<ChessMove> vMoves = new ArrayList<>();
        if(piece == null){
            return vMoves;
        }
        Collection<ChessMove> pMoves = piece.pieceMoves(curBoard,startPosition);
        //adding enpassant here
        if (enpassantable != null) {
            ChessPiece oldPiece = curBoard.getPiece(enpassantable.getEndPosition());
            if( oldPiece.getPieceType()== ChessPiece.PieceType.PAWN) {
                int lastMoveDif = enpassantable.getStartPosition().getRow() - enpassantable.getEndPosition().getRow();
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (lastMoveDif == 2 || lastMoveDif == -2) && enpassantable.getEndPosition().getRow()==startPosition.getRow()){
                    if(enpassantable.getEndPosition().getColumn()-startPosition.getColumn() == 1){
                        if (oldPiece.getTeamColor() == TeamColor.WHITE) {
                            ChessMove enpassant = new ChessMove(startPosition, new ChessPosition(enpassantable.getEndPosition().getRow() - 1, enpassantable.getEndPosition().getColumn()), null);
                            enpassant.setEnpassant(true);
                            vMoves.add(enpassant);
                        } else {
                            ChessMove enpassant = new ChessMove(startPosition, new ChessPosition(enpassantable.getEndPosition().getRow() + 1, enpassantable.getEndPosition().getColumn()), null);
                            enpassant.setEnpassant(true);
                            vMoves.add(enpassant);
                        }
                    } else if (enpassantable.getEndPosition().getColumn()-startPosition.getColumn() == -1) {
                        if (oldPiece.getTeamColor() == TeamColor.WHITE) {
                            ChessMove enpassant = new ChessMove(startPosition, new ChessPosition(enpassantable.getEndPosition().getRow() - 1, enpassantable.getEndPosition().getColumn()), null);
                            enpassant.setEnpassant(true);
                            vMoves.add(enpassant);
                        } else {
                            ChessMove enpassant = new ChessMove(startPosition, new ChessPosition(enpassantable.getEndPosition().getRow() + 1, enpassantable.getEndPosition().getColumn()), null);
                            enpassant.setEnpassant(true);
                            vMoves.add(enpassant);
                        }
                    }
                }
            }

        }

        for (ChessMove move : pMoves) {
            ChessGame copyGame = this.clone();
            copyGame.doMove(move);
            if (copyGame.isInCheck(piece.getTeamColor())) {
                continue;
            }
            vMoves.add(move);
        }
        return vMoves;
    }


    public void doMove(ChessMove move) {
        ChessPiece piece = curBoard.getPiece(move.getStartPosition());
        if (move.isEnpassant()) {
            curBoard.addPiece(enpassantable.getEndPosition(), null);
        } else if (move.isCastle()) {
            int colDif = move.getStartPosition().getColumn() - move.getEndPosition().getColumn();
        }
        if (move.getPromotionPiece() == null) {
            curBoard.addPiece(move.getEndPosition(), piece);
        } else {
            ChessPiece promoPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            curBoard.addPiece(move.getEndPosition(), promoPiece);
            }
        curBoard.addPiece(move.getStartPosition(), null);

    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        for (ChessMove vMove:validMoves) {
            if (vMove.equals(move)) {
                doMove(vMove);
                enpassantable = move;
            }
        }
        if (!validMoves.contains(move)){
            throw new InvalidMoveException();
        }
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        //ima try this line of thought
        //Things needed 1, teamColor King position
        // 2, an if statement that finds if one of the enemy moves == king position
        ChessPosition kingPosition = curBoard.findKing(teamColor);
        Collection<ChessPosition> enemyPositions = curBoard.findEnemies(teamColor);
        for (ChessPosition enemyPos:enemyPositions) {
            ChessPiece enemyPiece = curBoard.getPiece(enemyPos);
            Collection<ChessMove> attacks = enemyPiece.pieceMoves(curBoard,enemyPos);
            for (ChessMove move : attacks) {
                ChessPosition attack = move.getEndPosition();
                if(attack.equals(kingPosition))
                    return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        Collection<ChessPosition> myPositions;
        if (teamColor == TeamColor.BLACK){
            myPositions = curBoard.findEnemies(TeamColor.WHITE);
        } else {
            myPositions = curBoard.findEnemies(TeamColor.BLACK);
        }
        for (ChessPosition pos:myPositions) {
            ChessPiece myPiece = curBoard.getPiece(pos);
            Collection<ChessMove> myMoves = myPiece.pieceMoves(curBoard,pos);
            for (ChessMove move : myMoves) {
                ChessGame copyGame = this.clone();
                copyGame.doMove(move);
                if (!copyGame.isInCheck(myPiece.getTeamColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        List<ChessMove> count = new ArrayList<>();
        Collection<ChessPosition> myPositions;
        if (teamColor == TeamColor.BLACK){
            myPositions = curBoard.findEnemies(TeamColor.WHITE);
        } else {
            myPositions = curBoard.findEnemies(TeamColor.BLACK);
        }
        if (myPositions.isEmpty()){
            return false;
        }
        for (ChessPosition pos:myPositions) {
                count.addAll(validMoves(pos));
        }
        return count.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        curBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return curBoard;
    }

}
