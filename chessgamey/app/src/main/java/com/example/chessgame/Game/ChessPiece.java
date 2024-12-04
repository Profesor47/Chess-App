package com.example.chessgame.Game;

public class ChessPiece {
    private PieceType type;
    private boolean isWhite;
    private int row;
    private int col;
    private boolean hasMoved; // Track if piece has moved
    private int lastMoveCounter;

    public ChessPiece(PieceType type, boolean isWhite, int row, int col) {
        this.type = type;
        this.isWhite = isWhite;
        this.row = row;
        this.col = col;
        this.hasMoved = false;
        this.lastMoveCounter = -1;
    }
    private boolean justMadeDoubleMove;

    public void setJustMadeDoubleMove(boolean value) {
        this.justMadeDoubleMove = value;
    }

    public boolean isJustMadeDoubleMove() {
        return justMadeDoubleMove;
    }

    public int getImageResource() {
        String colorPrefix = isWhite ? "white_" : "black_";
        String pieceName = type.toString().toLowerCase();
        return getResourceByName(colorPrefix + pieceName);
    }

    private int getResourceByName(String name) {
        try {
            // Get the resource ID dynamically
            String packageName = "com.example.chessgame";
            return Class.forName(packageName + ".R$drawable")
                    .getField(name)
                    .getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Getters and setters
    public PieceType getType() { return type; }
    public boolean isWhite() { return isWhite; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
        this.hasMoved = true;
    }

}