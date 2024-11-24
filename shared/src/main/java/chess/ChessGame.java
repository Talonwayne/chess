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
    private boolean whiteCastleQueenside =true;
    private boolean whiteCastleKingside = true;
    private boolean blackCastleQueenside =true;
    private boolean blackCastleKingside = true;
    public ChessMove enpassantable;
    public boolean isGameOver;


    public ChessGame() {
        this.curBoard = new ChessBoard();
        curBoard.resetBoard();
        curTeam = TeamColor.WHITE;
        this.isGameOver = false;

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
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
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
            copy.curBoard = this.curBoard.clone();
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

    public void addEnPassant(Collection<ChessMove> vMoves, ChessPosition startPosition, ChessPiece piece){
        if (enpassantable != null) {
            ChessPiece oldPiece = curBoard.getPiece(enpassantable.getEndPosition());
            int lastMoveDif = enpassantable.getStartPosition().getRow() - enpassantable.getEndPosition().getRow();
            int colChange = enpassantable.getEndPosition().getColumn() - startPosition.getColumn();
            if(oldPiece != null && oldPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                        (lastMoveDif == 2 || lastMoveDif == -2) &&
                        enpassantable.getEndPosition().getRow()==startPosition.getRow() &&
                        (colChange == 1 || colChange == -1)){
                    ChessPosition newPosition =
                            new ChessPosition(enpassantable.getEndPosition().getRow() +
                                    (lastMoveDif/2), enpassantable.getEndPosition().getColumn());
                    ChessMove enpassant = new ChessMove(startPosition, newPosition, null);
                    enpassant.setEnpassant(true);
                    vMoves.add(enpassant);

                }
            }
        }
    }
    public void updateCorners(ChessPiece piece){
        ChessPiece temp = curBoard.getPiece(new ChessPosition(1,8));
        if (temp == null || (temp.getPieceType() != ChessPiece.PieceType.ROOK) && temp.getTeamColor() == piece.getTeamColor()) {
            whiteCastleKingside =false;
        }
        temp = curBoard.getPiece(new ChessPosition(1,1));
        if (temp == null || (temp.getPieceType() != ChessPiece.PieceType.ROOK) && temp.getTeamColor() == piece.getTeamColor()) {
            whiteCastleQueenside = false;
        }
        temp = curBoard.getPiece(new ChessPosition(8,8));
        if (temp == null || (temp.getPieceType() != ChessPiece.PieceType.ROOK) && temp.getTeamColor() == piece.getTeamColor()) {
            blackCastleKingside = false;
        }
        temp = curBoard.getPiece(new ChessPosition(8,1));
        if (temp == null || (temp.getPieceType() != ChessPiece.PieceType.ROOK) && temp.getTeamColor() == piece.getTeamColor()) {
            blackCastleQueenside = false;
        }

    }

    public void queensideCastle(Collection<ChessMove> vMoves, ChessPosition startPosition, ChessPiece piece, int row){
        ChessPosition n1 = new ChessPosition(row, 2);
        ChessPosition n2 = new ChessPosition(row, 3);
        ChessPosition n3 = new ChessPosition(row, 4);
        ChessPiece np1 = curBoard.getPiece(n1);
        ChessPiece np2 = curBoard.getPiece(n2);
        ChessPiece np3 = curBoard.getPiece(n3);
        if (np1 == null && np2 == null && np3 == null) {
            ChessGame copyGame = this.clone();
            ChessMove castle = new ChessMove(startPosition, new ChessPosition(row, 2), null);
            copyGame.doMove(castle);
            if (!copyGame.isInCheck(piece.getTeamColor())) {
                copyGame = this.clone();
                castle = new ChessMove(startPosition, new ChessPosition(row, 4), null);
                copyGame.doMove(castle);
                if (!copyGame.isInCheck(piece.getTeamColor())) {
                    copyGame = this.clone();
                    castle = new ChessMove(startPosition, new ChessPosition(row, 3), null);
                    copyGame.doMove(castle);
                    if (!copyGame.isInCheck(piece.getTeamColor())) {
                        castle.setCastle(true);
                        vMoves.add(castle);
                    }
                }
            }
        }
    }

    public void kingsideCastle(Collection<ChessMove> vMoves, ChessPosition startPosition, ChessPiece piece, int row){
        ChessPosition n1 = new ChessPosition(row,7);
        ChessPosition n2 = new ChessPosition(row,6);
        ChessPiece np1 = curBoard.getPiece(n1);
        ChessPiece np2 = curBoard.getPiece(n2);
        if(np1 == null && np2 == null){
            ChessGame copyGame = this.clone();
            ChessMove castle = new ChessMove(startPosition, new ChessPosition(row,6), null);
            copyGame.doMove(castle);
            if (!copyGame.isInCheck(piece.getTeamColor())) {
                copyGame = this.clone();
                castle = new ChessMove(startPosition, new ChessPosition(row,7), null);
                copyGame.doMove(castle);
                if (!copyGame.isInCheck(piece.getTeamColor())) {
                    castle.setCastle(true);
                    vMoves.add(castle);
                }
            }
        }
    }

    public void addCastling(Collection<ChessMove> vMoves, ChessPosition startPosition, ChessPiece piece){
        updateCorners(piece);
        if (piece.getPieceType() != ChessPiece.PieceType.KING){
            return;
        }
        if(whiteCastleKingside){
            kingsideCastle(vMoves,startPosition,piece,1);
        }
        if(blackCastleKingside){
            kingsideCastle(vMoves,startPosition,piece,8);
        }
        if(whiteCastleQueenside) {
           queensideCastle(vMoves,startPosition,piece,1);
        }
        if(blackCastleQueenside) {
            queensideCastle(vMoves,startPosition,piece,8);
        }
    }

    public void updateCastle(ChessMove move){
        ChessPiece piece = curBoard.getPiece(move.getEndPosition());
        if(piece != null) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    whiteCastleQueenside = false;
                    whiteCastleKingside = false;
                } else if (piece.getTeamColor() == TeamColor.BLACK){
                    blackCastleQueenside = false;
                    blackCastleKingside = false;
                }
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    if (move.getStartPosition().getColumn() == 1) {
                        whiteCastleQueenside = false;
                    } else if (move.getStartPosition().getColumn() == 8){
                        whiteCastleKingside = false;
                    }
                } else {
                    if (move.getStartPosition().getColumn() == 1) {
                        blackCastleQueenside = false;
                    } else if (move.getStartPosition().getColumn() == 8){
                        blackCastleKingside = false;
                    }
                }
            }
        }
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
        addEnPassant(vMoves,startPosition,piece);
        addCastling(vMoves,startPosition,piece);
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
    private void doMove(ChessMove move) {
        ChessPiece piece = curBoard.getPiece(move.getStartPosition());
        if (move.isEnpassant()) {
            curBoard.addPiece(enpassantable.getEndPosition(), null);
        } else if (move.isCastle()) {
            boolean isLeftCastle = (move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2;
            ChessPiece rook = new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK);
            ChessPosition rookPos = new ChessPosition(move.getStartPosition().getRow(), 6);
            ChessPosition oldRookPos = new ChessPosition(move.getStartPosition().getRow(), 8);
            if (isLeftCastle){
                oldRookPos = new ChessPosition(move.getStartPosition().getRow(), 1);
                rookPos = new ChessPosition(move.getStartPosition().getRow(),4);
            }
            curBoard.addPiece(oldRookPos, null);
            curBoard.addPiece(rookPos, rook);
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
        ChessPiece piece = curBoard.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        for (ChessMove vMove:validMoves) {
            if (vMove.equals(move)) {
                doMove(vMove);
                updateCastle(move);
                enpassantable = move;
                if(curTeam == TeamColor.WHITE){
                    setTeamTurn(TeamColor.BLACK);
                } else {
                    setTeamTurn(TeamColor.WHITE);
                }
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
        ChessPosition kingPosition = curBoard.findKing(teamColor);
        Collection<ChessPosition> enemyPositions = curBoard.findEnemies(teamColor);
        for (ChessPosition enemyPos:enemyPositions) {
            ChessPiece enemyPiece = curBoard.getPiece(enemyPos);
            Collection<ChessMove> attacks = enemyPiece.pieceMoves(curBoard,enemyPos);
            for (ChessMove move : attacks) {
                ChessPosition attack = move.getEndPosition();
                if(attack.equals(kingPosition)) {
                    return true;
                }
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
        whiteCastleKingside = true;
        whiteCastleQueenside = true;
        blackCastleQueenside = true;
        blackCastleKingside = true;
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