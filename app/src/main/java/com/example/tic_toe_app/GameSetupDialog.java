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
        
        selectedMode = "PVBOT";
        selectedSize = 3;
        selectedDifficulty = "EASY";
        
        difficultySection.setVisibility(View.VISIBLE);
        
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

        btnCancel.setOnClickListener(v -> dismiss());

        btnStart.setOnClickListener(v -> {
            startGame();
            dismiss();
        });
    }

    private void updateSelectedButtons() {
        if (selectedMode.equals("PVBOT")) {
            btnPvBot.setBackgroundResource(R.drawable.button_primary);
            btnPvP.setBackgroundResource(R.drawable.button_unselected);
        } else {
            btnPvBot.setBackgroundResource(R.drawable.button_unselected);
            btnPvP.setBackgroundResource(R.drawable.button_primary);
        }

        btnEasy.setBackgroundResource(R.drawable.button_unselected);
        btnNormal.setBackgroundResource(R.drawable.button_unselected);
        btnHard.setBackgroundResource(R.drawable.button_unselected);

        switch (selectedDifficulty) {
            case "EASY":
                btnEasy.setBackgroundResource(R.drawable.button_primary);
                break;
            case "NORMAL":
                btnNormal.setBackgroundResource(R.drawable.button_primary);
                break;
            case "HARD":
                btnHard.setBackgroundResource(R.drawable.button_primary);
                break;
        }

        btn3x3.setBackgroundResource(R.drawable.button_unselected);
        btn5x5.setBackgroundResource(R.drawable.button_unselected);
        btn7x7.setBackgroundResource(R.drawable.button_unselected);

        switch (selectedSize) {
            case 3:
                btn3x3.setBackgroundResource(R.drawable.button_primary);
                break;
            case 5:
                btn5x5.setBackgroundResource(R.drawable.button_primary);
                break;
            case 7:
                btn7x7.setBackgroundResource(R.drawable.button_primary);
                break;
        }
    }

    private void startGame() {
        Intent intent = new Intent(getContext(), GameActivity.class);
        intent.putExtra("GAME_MODE", selectedMode);
        intent.putExtra("DIFFICULTY", selectedDifficulty);
        intent.putExtra("BOARD_SIZE", String.valueOf(selectedSize));
        getContext().startActivity(intent);
    }
}