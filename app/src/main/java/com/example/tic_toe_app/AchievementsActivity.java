package com.example.tic_toe_app;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AchievementsActivity extends AppCompatActivity {

    private TextView winProgressText, loseProgressText;
    private ProgressBar winProgressBar, loseProgressBar;
    private Button btnBack, btnStatisticalAnalysis;
    private MusicManager musicManager;
    
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TicTacToeStats";
    
    private static final String KEY_WINS = "bot_wins";
    private static final String KEY_LOSSES = "bot_losses";
    
    private static final String KEY_EASY_WINS = "easy_wins";
    private static final String KEY_EASY_LOSSES = "easy_losses";
    private static final String KEY_MEDIUM_WINS = "medium_wins";
    private static final String KEY_MEDIUM_LOSSES = "medium_losses";
    private static final String KEY_HARD_WINS = "hard_wins";
    private static final String KEY_HARD_LOSSES = "hard_losses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_achievements);
        
        musicManager = MusicManager.getInstance(this);
        
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
        btnStatisticalAnalysis = findViewById(R.id.btnStatisticalAnalysis);
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        btnBack.setOnClickListener(v -> finish());
        btnStatisticalAnalysis.setOnClickListener(v -> showStatisticalAnalysisDialog());
    }

    private void loadAchievementProgress() {
        int wins = sharedPreferences.getInt(KEY_WINS, 0);
        int losses = sharedPreferences.getInt(KEY_LOSSES, 0);
        int totalGames = wins + losses;
        
        updateAchievement(winProgressText, winProgressBar, wins, 100, "wins");
        updateAchievement(loseProgressText, loseProgressBar, losses, 100, "losses");
    }

    private void updateAchievement(TextView progressText, ProgressBar progressBar, 
                                 int current, int target, String type) {
        int displayCurrent = Math.min(current, target);
        
        progressText.setText(displayCurrent + "/" + target);
        
        progressBar.setProgress(displayCurrent);
        progressBar.setMax(target);
        
        if (displayCurrent >= target) {
            progressText.setTextColor(getResources().getColor(R.color.button_achievement, null));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicManager != null) {
            musicManager.onActivityResume();
        }
        loadAchievementProgress();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (musicManager != null) {
            musicManager.onActivityPause();
        }
    }

    public static void updateGameStats(android.content.Context context, boolean playerWon, String difficulty) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        if (playerWon) {
            int currentWins = prefs.getInt(KEY_WINS, 0);
            editor.putInt(KEY_WINS, currentWins + 1);
        } else {
            int currentLosses = prefs.getInt(KEY_LOSSES, 0);
            editor.putInt(KEY_LOSSES, currentLosses + 1);
        }
        
        String winKey = "";
        String lossKey = "";
        
        switch (difficulty.toLowerCase()) {
            case "easy":
                winKey = KEY_EASY_WINS;
                lossKey = KEY_EASY_LOSSES;
                break;
            case "medium":
                winKey = KEY_MEDIUM_WINS;
                lossKey = KEY_MEDIUM_LOSSES;
                break;
            case "hard":
                winKey = KEY_HARD_WINS;
                lossKey = KEY_HARD_LOSSES;
                break;
        }
        
        if (!winKey.isEmpty()) {
            if (playerWon) {
                int currentWins = prefs.getInt(winKey, 0);
                editor.putInt(winKey, currentWins + 1);
            } else {
                int currentLosses = prefs.getInt(lossKey, 0);
                editor.putInt(lossKey, currentLosses + 1);
            }
        }
        
        editor.apply();
    }
    
    public static void updateGameStats(android.content.Context context, boolean playerWon) {
        updateGameStats(context, playerWon, "medium");
    }
    
    public static int[] getCurrentStats(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int wins = prefs.getInt(KEY_WINS, 0);
        int losses = prefs.getInt(KEY_LOSSES, 0);
        return new int[]{wins, losses, wins + losses};
    }
    
    private void showStatisticalAnalysisDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_statistical_analysis);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        TextView tvEasyWins = dialog.findViewById(R.id.tvEasyWins);
        TextView tvEasyLosses = dialog.findViewById(R.id.tvEasyLosses);
        TextView tvEasyWinRate = dialog.findViewById(R.id.tvEasyWinRate);
        
        TextView tvMediumWins = dialog.findViewById(R.id.tvMediumWins);
        TextView tvMediumLosses = dialog.findViewById(R.id.tvMediumLosses);
        TextView tvMediumWinRate = dialog.findViewById(R.id.tvMediumWinRate);
        
        TextView tvHardWins = dialog.findViewById(R.id.tvHardWins);
        TextView tvHardLosses = dialog.findViewById(R.id.tvHardLosses);
        TextView tvHardWinRate = dialog.findViewById(R.id.tvHardWinRate);
        
        TextView tvTotalGames = dialog.findViewById(R.id.tvTotalGames);
        TextView tvTotalWins = dialog.findViewById(R.id.tvTotalWins);
        TextView tvTotalLosses = dialog.findViewById(R.id.tvTotalLosses);
        TextView tvOverallWinRate = dialog.findViewById(R.id.tvOverallWinRate);
        
        int easyWins = sharedPreferences.getInt(KEY_EASY_WINS, 0);
        int easyLosses = sharedPreferences.getInt(KEY_EASY_LOSSES, 0);
        int mediumWins = sharedPreferences.getInt(KEY_MEDIUM_WINS, 0);
        int mediumLosses = sharedPreferences.getInt(KEY_MEDIUM_LOSSES, 0);
        int hardWins = sharedPreferences.getInt(KEY_HARD_WINS, 0);
        int hardLosses = sharedPreferences.getInt(KEY_HARD_LOSSES, 0);
        
        int totalWins = easyWins + mediumWins + hardWins;
        int totalLosses = easyLosses + mediumLosses + hardLosses;
        int totalGames = totalWins + totalLosses;
        
        tvEasyWins.setText(String.valueOf(easyWins));
        tvEasyLosses.setText(String.valueOf(easyLosses));
        tvEasyWinRate.setText(calculateWinRate(easyWins, easyLosses));
        
        tvMediumWins.setText(String.valueOf(mediumWins));
        tvMediumLosses.setText(String.valueOf(mediumLosses));
        tvMediumWinRate.setText(calculateWinRate(mediumWins, mediumLosses));
        
        tvHardWins.setText(String.valueOf(hardWins));
        tvHardLosses.setText(String.valueOf(hardLosses));
        tvHardWinRate.setText(calculateWinRate(hardWins, hardLosses));
        
        tvTotalGames.setText(String.valueOf(totalGames));
        tvTotalWins.setText(String.valueOf(totalWins));
        tvTotalLosses.setText(String.valueOf(totalLosses));
        tvOverallWinRate.setText(calculateWinRate(totalWins, totalLosses));
        
        Button btnDeleteData = dialog.findViewById(R.id.btnDeleteData);
        Button btnBack = dialog.findViewById(R.id.btnBack);
        
        btnDeleteData.setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmationDialog();
        });
        
        btnBack.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private String calculateWinRate(int wins, int losses) {
        int totalGames = wins + losses;
        if (totalGames == 0) return "0%";
        
        double winRate = (double) wins / totalGames * 100;
        return String.format("%.1f%%", winRate);
    }
    
    private void showDeleteConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete_confirmation);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        Button btnConfirmDelete = dialog.findViewById(R.id.btnConfirmDelete);
        Button btnCancelDelete = dialog.findViewById(R.id.btnCancelDelete);
        
        btnConfirmDelete.setOnClickListener(v -> {
            deleteAllGameData();
            dialog.dismiss();
            Toast.makeText(this, "All game data deleted successfully!", Toast.LENGTH_LONG).show();
        });
        
        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void deleteAllGameData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.remove(KEY_WINS);
        editor.remove(KEY_LOSSES);
        editor.remove(KEY_EASY_WINS);
        editor.remove(KEY_EASY_LOSSES);
        editor.remove(KEY_MEDIUM_WINS);
        editor.remove(KEY_MEDIUM_LOSSES);
        editor.remove(KEY_HARD_WINS);
        editor.remove(KEY_HARD_LOSSES);
        
        editor.apply();
        
        SharedPreferences scoresPrefs = getSharedPreferences("TicToeScores", MODE_PRIVATE);
        SharedPreferences.Editor scoresEditor = scoresPrefs.edit();
        scoresEditor.clear();
        scoresEditor.apply();
        
        loadAchievementProgress();
    }
}