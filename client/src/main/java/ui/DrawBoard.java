package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class DrawBoard {
    private static final int NUM_SQUARES_XY = 8;
    private static final String BG = EscapeSequences.SET_BG_COLOR_WHITE;
    private static final String BLACK_SQUARE = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String WHITE_SQUARE = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

    private boolean isWhite;

    public DrawBoard(boolean isWhite){
        this.isWhite = isWhite;
    }

    public void drawBoard(ChessGame game){
        String[] blackSymbols = {"a","b","c","d","e","f","g","h"};
        String[] whiteSymbols = {"h","g","f","e","d","c","b","a"};

        if (isWhite){
            String[] symbols = {"h","g","f","e","d","c","b","a"};
            drawMargin(symbols);

            drawMargin(symbols);

        } else {
            String[] symbols = {"a","b","c","d","e","f","g","h"};
            drawMargin(symbols);

            drawMargin(symbols);

        }

    }

    public void drawMargin(String[] symbols){
        System.out.print(BG);
        for (String symbol: symbols){
            System.out.print(symbol);
        }
    }

    public void drawRow(int row, ChessGame game){
        System.out.print(BG);
        System.out.print(row);
        for (int i = 1; i < 9; i++){
            ChessPosition square = new ChessPosition(row,i);
            ChessPiece piece = game.getBoard().getPiece(square);
            drawSquare(square,piece);
        }
        System.out.print(BG);
        System.out.print(row);
    }

    public void drawSquare(ChessPosition square, ChessPiece piece){
        if (square.getColumn() % 2 == 1){
            System.out.print();
        }
    }
}
