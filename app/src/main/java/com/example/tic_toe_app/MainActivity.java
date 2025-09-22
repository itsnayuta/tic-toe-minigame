package com.example.tic_toe_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private Button btnPlay, btnSettings, btnAchievements, btnExit;
    private MusicManager musicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        musicManager = MusicManager.getInstance(this);
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
        btnPlay.setOnClickListener(v -> showGameSetupDialog());
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnAchievements.setOnClickListener(v -> startActivity(new Intent(this, AchievementsActivity.class)));
        btnExit.setOnClickListener(v -> showExitDialog());
    }

    private void showGameSetupDialog() {
        GameSetupDialog dialog = new GameSetupDialog(this);
        dialog.show();
    }
    
    private void showExitDialog() {
        new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicManager != null) {
            musicManager.onActivityResume();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (musicManager != null) {
            musicManager.onActivityPause();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicManager != null) {
            musicManager.onAppDestroy();
        }
    }
}