package com.example.chessgame.Game;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.CountDownTimer;


public class GameStatusManager {
    private TextView turnTextView;
    private TextView statusTextView;
    private TextView whiteTimerTextView;
    private TextView blackTimerTextView;
    private ProgressBar whiteTimerProgress;
    private ProgressBar blackTimerProgress;
    private GameState gameState;

    private CountDownTimer whiteTimer;
    private CountDownTimer blackTimer;
    private long whiteTimeLeft = 300000; // 5 minutes in milliseconds
    private long blackTimeLeft = 300000;

    public GameStatusManager(TextView turnTextView, TextView statusTextView,
                             TextView whiteTimerTextView, TextView blackTimerTextView,
                             ProgressBar whiteTimerProgress, ProgressBar blackTimerProgress,
                             GameState gameState) {
        this.turnTextView = turnTextView;
        this.statusTextView = statusTextView;
        this.whiteTimerTextView = whiteTimerTextView;
        this.blackTimerTextView = blackTimerTextView;
        this.whiteTimerProgress = whiteTimerProgress;
        this.blackTimerProgress = blackTimerProgress;
        this.gameState = gameState;

        updateDisplay();
    }

    public void updateDisplay() {
        if (gameState.isGameOver()) {
            turnTextView.setText("Game Over!");
            statusTextView.setText(gameState.getWinner() + " wins!");
        } else {
            turnTextView.setText(gameState.isWhiteTurn() ? "White's Turn" : "Black's Turn");
            statusTextView.setText("");

            // Highlight the active timer
            if (gameState.isWhiteTurn()) {
                whiteTimerTextView.setBackgroundColor(android.graphics.Color.GREEN);
                blackTimerTextView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            } else {
                blackTimerTextView.setBackgroundColor(android.graphics.Color.RED);
                whiteTimerTextView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }
        }
    }

    public void announceCheck() {
        statusTextView.setText("Check!");
    }

    public void announceCheckmate(boolean whiteWins) {
        gameState.setGameOver(true);
        gameState.setWinner(whiteWins ? "White" : "Black");
        updateDisplay();
    }

    public void announceStalemate() {
        gameState.setGameOver(true);
        gameState.setWinner("Draw");
        turnTextView.setText("Game Over!");
        statusTextView.setText("Stalemate - Draw!");
    }

    private void startWhiteTimer() {
        stopTimers(); // Stop any existing timers
        whiteTimer = new CountDownTimer(whiteTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                whiteTimeLeft = millisUntilFinished;
                updateWhiteTimerDisplay();
                whiteTimerProgress.setProgress((int) (whiteTimeLeft / 1000)); // Update progress bar
            }

            @Override
            public void onFinish() {
                gameState.endGameByTimeout(true);
                updateDisplay();
            }
        }.start();
    }

    private void startBlackTimer() {
        stopTimers(); // Stop any existing timers
        blackTimer = new CountDownTimer(blackTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                blackTimeLeft = millisUntilFinished;
                updateBlackTimerDisplay();
                blackTimerProgress.setProgress((int) (blackTimeLeft / 1000)); // Update progress bar
            }

            @Override
            public void onFinish() {
                gameState.endGameByTimeout(false);
                updateDisplay();
            }
        }.start();
    }

    private void stopTimers() {
        if (whiteTimer != null) {
            whiteTimer.cancel();
        }
        if (blackTimer != null) {
            blackTimer.cancel();
        }
    }

    private void updateWhiteTimerDisplay() {
        int minutes = (int) (whiteTimeLeft / 1000) / 60;
        int seconds = (int) (whiteTimeLeft / 1000) % 60;
        whiteTimerTextView.setText(String.format("White Time: %02d:%02d", minutes, seconds));
    }

    private void updateBlackTimerDisplay() {
        int minutes = (int) (blackTimeLeft / 1000) / 60;
        int seconds = (int) (blackTimeLeft / 1000) % 60;
        blackTimerTextView.setText(String.format("Black Time: %02d:%02d", minutes, seconds));
    }

    public void setTimers(long timeInMillis) {
        whiteTimeLeft = timeInMillis;
        blackTimeLeft = timeInMillis;
        whiteTimerProgress.setMax((int) (timeInMillis / 1000)); // Set max progress for bars
        blackTimerProgress.setMax((int) (timeInMillis / 1000));
        updateWhiteTimerDisplay();
        updateBlackTimerDisplay();
    }

    public void resetTimers() {
        long defaultTimerValue = 30 * 60 * 1000; // 30 minutes in milliseconds
        setTimers(defaultTimerValue);
        stopTimers(); // Ensure any running timers are stopped
    }
    public void toggleTurn() {
        stopTimers(); // Stop the current timer

        // Start the timer for the next player's turn
        if (gameState.isWhiteTurn()) {
            startWhiteTimer();
        } else {
            startBlackTimer();
        }
    }


}
