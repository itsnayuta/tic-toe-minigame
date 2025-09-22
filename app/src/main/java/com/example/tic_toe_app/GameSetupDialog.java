package com.example.tic_toe_app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameSetupDialog extends Dialog {

    private Button btnPvP, btnPvBot;
    private Button btnEasy, btnNormal, btnHard;
    private Button btn3x3, btn5x5, btn7x7;
    private Button btnCancel, btnStart;
    private LinearLayout difficultySection;

    private String selectedMode = "PVP";
    private String selectedDifficulty = "EASY";
    private int selectedSize = 3;

    public GameSetupDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_game_setup);
        
        initializeViews();
        setupClickListeners();
        
        // Set initial selections: 1P mode, Easy difficulty, and 3x3 board
        selectedMode = "PVBOT";
        selectedSize = 3;
        selectedDifficulty = "EASY";
        
        // Show difficulty section initially (since PvBot is default)
        difficultySection.setVisibility(View.VISIBLE);
        
        // Force initial update after a brief delay to ensure views are ready
        getWindow().getDecorView().post(() -> updateSelectedButtons());
    }

    private void initializeViews() {
        btnPvP = findViewById(R.id.btnPvP);
        btnPvBot = findViewById(R.id.btnPvBot);
        
        btnEasy = findViewById(R.id.btnEasy);
        btnNormal = findViewById(R.id.btnNormal);
        btnHard = findViewById(R.id.btnHard);
        
        btn3x3 = findViewById(R.id.btn3x3);
        btn5x5 = findViewById(R.id.btn5x5);
        btn7x7 = findViewById(R.id.btn7x7);
        
        btnCancel = findViewById(R.id.btnCancel);
        btnStart = findViewById(R.id.btnStart);
        
        difficultySection = findViewById(R.id.difficultySection);
    }

    private void setupClickListeners() {
        // Game Mode buttons
        btnPvBot.setOnClickListener(v -> {
            selectedMode = "PVBOT";
            difficultySection.setVisibility(View.VISIBLE);
            updateSelectedButtons();
        });

        btnPvP.setOnClickListener(v -> {
            selectedMode = "PVP";
            difficultySection.setVisibility(View.GONE);
            updateSelectedButtons();
        });

        // Difficulty buttons
        btnEasy.setOnClickListener(v -> {
            selectedDifficulty = "EASY";
            updateSelectedButtons();
        });

        btnNormal.setOnClickListener(v -> {
            selectedDifficulty = "NORMAL";
            updateSelectedButtons();
        });

        btnHard.setOnClickListener(v -> {
            selectedDifficulty = "HARD";
            updateSelectedButtons();
        });

        // Board size buttons
        btn3x3.setOnClickListener(v -> {
            selectedSize = 3;
            updateSelectedButtons();
        });

        btn5x5.setOnClickListener(v -> {
            selectedSize = 5;
            updateSelectedButtons();
        });

        btn7x7.setOnClickListener(v -> {
            selectedSize = 7;
            updateSelectedButtons();
        });

        // Action buttons
        btnCancel.setOnClickListener(v -> dismiss());

        btnStart.setOnClickListener(v -> {
            startGame();
            dismiss();
        });
    }

    private void updateSelectedButtons() {
        // Update game mode buttons
        if (selectedMode.equals("PVBOT")) {
            btnPvBot.setBackgroundResource(R.drawable.button_primary);  // Hồng cho selected
            btnPvP.setBackgroundResource(R.drawable.button_unselected); // Xám cho unselected
        } else {
            btnPvBot.setBackgroundResource(R.drawable.button_unselected);   // Xám cho unselected
            btnPvP.setBackgroundResource(R.drawable.button_primary);   // Hồng cho selected
        }

        // Update difficulty buttons - reset all first
        btnEasy.setBackgroundResource(R.drawable.button_unselected);
        btnNormal.setBackgroundResource(R.drawable.button_unselected);
        btnHard.setBackgroundResource(R.drawable.button_unselected);

        // Highlight selected difficulty with pink
        switch (selectedDifficulty) {
            case "EASY":
                btnEasy.setBackgroundResource(R.drawable.button_primary);  // Hồng
                break;
            case "NORMAL":
                btnNormal.setBackgroundResource(R.drawable.button_primary); // Hồng
                break;
            case "HARD":
                btnHard.setBackgroundResource(R.drawable.button_primary);   // Hồng
                break;
        }

        // Update board size buttons - reset all first
        btn3x3.setBackgroundResource(R.drawable.button_unselected);
        btn5x5.setBackgroundResource(R.drawable.button_unselected);
        btn7x7.setBackgroundResource(R.drawable.button_unselected);

        // Highlight selected board size with pink
        switch (selectedSize) {
            case 3:
                btn3x3.setBackgroundResource(R.drawable.button_primary);  // Hồng
                break;
            case 5:
                btn5x5.setBackgroundResource(R.drawable.button_primary);  // Hồng
                break;
            case 7:
                btn7x7.setBackgroundResource(R.drawable.button_primary);  // Hồng
                break;
        }
    }

    private void startGame() {
        Intent intent = new Intent(getContext(), GameActivity.class);
        intent.putExtra("MODE", selectedMode);
        intent.putExtra("DIFFICULTY", selectedDifficulty);
        intent.putExtra("SIZE", selectedSize);
        getContext().startActivity(intent);
    }
}