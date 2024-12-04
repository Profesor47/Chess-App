package com.example.chessgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chessgame.Game.ChessBoard;
import com.example.chessgame.Game.GameState;
import com.example.chessgame.Game.GameStatusManager;

public class MainActivity extends AppCompatActivity {
    private ChessBoard chessBoard;
    private GameState gameState;
    private GameStatusManager gameStatusManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve timer value from intent
        int timerMinutes = getIntent().getIntExtra("TIMER", 30); // Default to 30 minutes
        long timerMillis = timerMinutes * 60 * 1000;

        // Initialize Game State and UI Components
        gameState = new GameState();

        TextView turnTextView = findViewById(R.id.current_turn_text);
        TextView statusTextView = findViewById(R.id.game_status_text);
        TextView whiteTimerTextView = findViewById(R.id.white_timer_text);
        TextView blackTimerTextView = findViewById(R.id.black_timer_text);
        ProgressBar whiteTimerProgress = findViewById(R.id.white_timer_progress);
        ProgressBar blackTimerProgress = findViewById(R.id.black_timer_progress);
        GridLayout gridLayout = findViewById(R.id.chess_grid);

        gameStatusManager = new GameStatusManager(turnTextView, statusTextView,
                whiteTimerTextView, blackTimerTextView,
                whiteTimerProgress, blackTimerProgress,
                gameState);

        chessBoard = new ChessBoard(this, gridLayout, gameState, gameStatusManager);

        // Set the initial timer value for both players
        gameStatusManager.setTimers(timerMillis);

        // Reset Button Logic
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            gameState = new GameState(); // Reset the game state
            chessBoard.resetBoard(); // Reset the chessboard
            gameStatusManager.resetTimers(); // Reset the timers
            gameStatusManager.updateDisplay(); // Update the UI display
        });
    }
}
