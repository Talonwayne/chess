package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class DrawBoard {

    private boolean isWhite;
    private boolean isHighlight = false;
    private List<ChessPosition> highlights;

    public DrawBoard(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setWhite(boolean isViewColorWhite) {
        isWhite = isViewColorWhite;
    }

    public void drawBoard(ChessGame game) {
        String[] symbols = isWhite ? new String[]{"a", "b", "c", "d", "e", "f", "g", "h"} : new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        drawMargin(symbols);
        int[] rows = isWhite ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        for (int row: rows) {
            drawRow(row,game);
            System.out.println();
        }
        drawMargin(symbols);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private void drawMargin(String[] symbols) {
        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.print("  ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        for (String symbol : symbols) {
            System.out.print(" " + symbol + " ");
        }
        System.out.print(EscapeSequences.EMPTY);
        System.out.println();
    }

    private void drawRow(int row, ChessGame game) {
        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(row + " ");
        int[] cols = isWhite ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};
        for (int col: cols) {
            ChessPosition square = new ChessPosition(row, col);
            ChessPiece piece = game.getBoard().getPiece(square);
            drawSquare(square, piece);
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" " + row);
        System.out.print(EscapeSequences.EMPTY);
    }

    private void drawSquare(ChessPosition square, ChessPiece piece) {
        String squareColor = (square.getRow() + square.getColumn()) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        if (isHighlight){
            if(highlights.contains(square)){
                System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
            } else {
                System.out.print(squareColor);
            }
        } else {
            System.out.print(squareColor);
        }
        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
            return;
        }

        ChessPiece.PieceType pieceType = piece.getPieceType();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            switch (pieceType) {
                case PAWN ->   System.out.print(EscapeSequences.WHITE_PAWN);
                case ROOK ->   System.out.print(EscapeSequences.WHITE_ROOK);
                case BISHOP -> System.out.print(EscapeSequences.WHITE_BISHOP);
                case QUEEN ->  System.out.print(EscapeSequences.WHITE_QUEEN);
                case KNIGHT -> System.out.print(EscapeSequences.WHITE_KNIGHT);
                case KING ->   System.out.print(EscapeSequences.WHITE_KING);
                case null, default -> System.out.print(EscapeSequences.EMPTY);
            }
        } else {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            switch (pieceType) {
                case PAWN ->   System.out.print(EscapeSequences.BLACK_PAWN);
                case ROOK ->   System.out.print(EscapeSequences.BLACK_ROOK);
                case BISHOP -> System.out.print(EscapeSequences.BLACK_BISHOP);
                case QUEEN ->  System.out.print(EscapeSequences.BLACK_QUEEN);
                case KNIGHT -> System.out.print(EscapeSequences.BLACK_KNIGHT);
                case KING ->   System.out.print(EscapeSequences.BLACK_KING);
                case null, default -> System.out.print(EscapeSequences.EMPTY);
            }
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    public void displayPossibleMoves(ChessGame game,ChessPosition position){
        isHighlight = true;
        ChessPiece piece = game.getBoard().getPiece(position);
        Collection<ChessMove> moves = piece.pieceMoves(game.getBoard(),position);
        highlights.clear();
        for (ChessMove move: moves){
            highlights.add(move.getEndPosition());
        }
        drawBoard(game);
        isHighlight = false;
    }
}
