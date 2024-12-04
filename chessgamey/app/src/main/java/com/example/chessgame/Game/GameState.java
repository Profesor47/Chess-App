package com.example.chessgame.Game;
import java.util.ArrayList;
import java.util.List;
public class GameState {
    private boolean isWhiteTurn;
    private ChessPiece selectedPiece;
    private boolean isGameOver;
    private String winner;

    private List<Move> moveHistory = new ArrayList<>();
    public void addMove(Move move) {
        moveHistory.add(move);
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }
    public GameState() {
        isWhiteTurn = true;
        selectedPiece = null;
        isGameOver = false;
        winner = null;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public void toggleTurn() {
        isWhiteTurn = !isWhiteTurn;
    }

    public ChessPiece getSelectedPiece() {
        return selectedPiece;
    }

    public void setSelectedPiece(ChessPiece piece) {
        this.selectedPiece = piece;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
    public void endGameByTimeout(boolean whiteTimedOut) {
        isGameOver = true;
        winner = whiteTimedOut ? "Black" : "White";
    }
}