// New class: Move.java
package com.example.chessgame.Game;

public class Move {
    private final ChessPiece piece;
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final ChessPiece capturedPiece;

    public Move(ChessPiece piece, int fromRow, int fromCol, int toRow, int toCol, ChessPiece capturedPiece) {
        this.piece = piece;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.capturedPiece = capturedPiece;
    }

    public String getNotation() {
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] rows = {"8", "7", "6", "5", "4", "3", "2", "1"};

        String pieceSymbol = getPieceSymbol();
        String capture = capturedPiece != null ? "x" : "";

        return String.format("%s%s%s%s%s",
                pieceSymbol,
                cols[fromCol],
                rows[fromRow],
                capture,
                cols[toCol] + rows[toRow]);
    }

    private String getPieceSymbol() {
        switch (piece.getType()) {
            case KING: return "K";
            case QUEEN: return "Q";
            case ROOK: return "R";
            case BISHOP: return "B";
            case KNIGHT: return "N";
            case PAWN: return "";
            default: return "";
        }
    }
}

