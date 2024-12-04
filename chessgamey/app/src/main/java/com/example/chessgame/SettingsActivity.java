package com.example.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Find Views
        EditText timerInput = findViewById(R.id.timer_input);
        Button playButton = findViewById(R.id.play_button);
        Button preset5Min = findViewById(R.id.preset_5min);
        Button preset10Min = findViewById(R.id.preset_10min);
        Button preset15Min = findViewById(R.id.preset_15min);

        // Preset Button Logic
        preset5Min.setOnClickListener(v -> timerInput.setText("5")); // Set timer to 5 minutes
        preset10Min.setOnClickListener(v -> timerInput.setText("10")); // Set timer to 10 minutes
        preset15Min.setOnClickListener(v -> timerInput.setText("15")); // Set timer to 15 minutes

        // Play Button Logic
        playButton.setOnClickListener(v -> {
            String input = timerInput.getText().toString();
            if (!input.isEmpty()) {
                try {
                    int timerMinutes = Integer.parseInt(input);
                    if (timerMinutes > 0 && timerMinutes <= 30) {
                        // Pass timer value to MainActivity
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        intent.putExtra("TIMER", timerMinutes);
                        startActivity(intent);
                    } else {
                        showToast("Enter a value between 1 and 30 minutes.");
                    }
                } catch (NumberFormatException e) {
                    showToast("Invalid number. Please enter a valid timer value.");
                }
            } else {
                showToast("Please enter a timer value.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
