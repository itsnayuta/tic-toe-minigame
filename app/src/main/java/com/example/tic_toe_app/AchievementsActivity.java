package com.example.tic_toe_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AchievementsActivity extends AppCompatActivity {

    private TextView winProgressText, loseProgressText;
    private ProgressBar winProgressBar, loseProgressBar;
    private Button btnBack;
    
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TicTacToeStats";
    private static final String KEY_WINS = "bot_wins";
    private static final String KEY_LOSSES = "bot_losses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_achievements);
        
        initializeViews();
        loadAchievementProgress();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        winProgressText = findViewById(R.id.winProgressText);
        loseProgressText = findViewById(R.id.loseProgressText);
        winProgressBar = findViewById(R.id.winProgressBar);
        loseProgressBar = findViewById(R.id.loseProgressBar);
        btnBack = findViewById(R.id.btnBack);
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Back button listener
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAchievementProgress() {
        // Get current progress from SharedPreferences
        int wins = sharedPreferences.getInt(KEY_WINS, 0);
        int losses = sharedPreferences.getInt(KEY_LOSSES, 0);
        
        // Update Victory Master achievement (Win 100 matches)
        updateAchievement(winProgressText, winProgressBar, wins, 100, "wins");
        
        // Update Never Give Up achievement (Lose 100 matches)
        updateAchievement(loseProgressText, loseProgressBar, losses, 100, "losses");
    }

    private void updateAchievement(TextView progressText, ProgressBar progressBar, 
                                 int current, int target, String type) {
        // Ensure current doesn't exceed target
        int displayCurrent = Math.min(current, target);
        
        // Update progress text
        progressText.setText(displayCurrent + "/" + target);
        
        // Update progress bar
        progressBar.setProgress(displayCurrent);
        progressBar.setMax(target);
        
        // If achievement is completed, you could add special effects here
        if (displayCurrent >= target) {
            // Achievement completed - could show different color or effect
            progressText.setTextColor(getResources().getColor(R.color.button_achievement, null));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh progress when returning to this activity
        loadAchievementProgress();
    }

    // Method to update stats (to be called from GameActivity)
    public static void updateGameStats(android.content.Context context, boolean playerWon) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        if (playerWon) {
            int currentWins = prefs.getInt(KEY_WINS, 0);
            editor.putInt(KEY_WINS, currentWins + 1);
        } else {
            int currentLosses = prefs.getInt(KEY_LOSSES, 0);
            editor.putInt(KEY_LOSSES, currentLosses + 1);
        }
        
        editor.apply();
    }
}