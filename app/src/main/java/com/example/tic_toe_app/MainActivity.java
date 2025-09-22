package com.example.tic_toe_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private Button btnPlay, btnSettings, btnAchievements, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupClickListeners();
    }
    
    private void initializeViews() {
        btnPlay = findViewById(R.id.btnPlay);
        btnSettings = findViewById(R.id.btnSettings);
        btnAchievements = findViewById(R.id.btnAchievements);
        btnExit = findViewById(R.id.btnExit);
    }
    
    private void setupClickListeners() {
        btnPlay.setOnClickListener(v -> showGameModeDialog());
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnAchievements.setOnClickListener(v -> startActivity(new Intent(this, AchievementsActivity.class)));
        btnExit.setOnClickListener(v -> showExitDialog());
    }
    
    private void showGameModeDialog() {
        final String[] modes = {"Player vs Player", "Player vs Bot"};
        
        new AlertDialog.Builder(this)
                .setTitle("Choose Game Mode")
                .setItems(modes, (dialog, which) -> {
                    if (which == 0) {
                        chooseBoardAndStart("PVP", "EASY"); // PvP doesn't need difficulty
                    } else {
                        chooseDifficulty(difficulty -> chooseBoardAndStart("PVBOT", difficulty));
                    }
                })
                .show();
    }
    
    private interface DifficultyCallback {
        void onChosen(String difficulty);
    }
    
    private void chooseDifficulty(DifficultyCallback callback) {
        final String[] difficulties = {"Easy", "Normal", "Hard"};
        final String[] difficultyValues = {"EASY", "NORMAL", "HARD"};
        
        new AlertDialog.Builder(this)
                .setTitle("Choose Bot Difficulty")
                .setItems(difficulties, (dialog, which) -> callback.onChosen(difficultyValues[which]))
                .show();
    }
    
    private void chooseBoardAndStart(String mode, String difficulty) {
        final String[] sizes = {"3 x 3", "5 x 5", "7 x 7"};
        
        new AlertDialog.Builder(this)
                .setTitle("Choose Board Size")
                .setItems(sizes, (dialog, which) -> {
                    int size = 3;
                    if (which == 1) size = 5;
                    else if (which == 2) size = 7;
                    
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("MODE", mode);
                    intent.putExtra("DIFFICULTY", difficulty);
                    intent.putExtra("SIZE", size);
                    startActivity(intent);
                })
                .show();
    }
    
    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Add music manager
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Add music manager
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: Add music manager
    }
}