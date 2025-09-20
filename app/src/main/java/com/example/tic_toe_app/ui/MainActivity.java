package com.example.tic_toe_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.example.tic_toe_app.R;
import com.example.tic_toe_app.utils.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnExit = findViewById(R.id.btnExit);


        btnPlay.setOnClickListener(v -> showModeDialog());


        btnSettings.setOnClickListener(v -> {
            // TODO: open SettingsActivity (implemented in later steps)
        });


        btnHistory.setOnClickListener(v -> {
            // TODO: open HistoryActivity (implemented later)
        });


        btnExit.setOnClickListener(v -> finish());
    }


    private void showModeDialog() {
        final String[] modes = {"Player vs Player", "Player vs Bot"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose mode")
                .setItems(modes, (dialog, which) -> {
                    String mode = (which == 0) ? Constants.MODE_PVP : Constants.MODE_PVB;
                    showSizeDialog(mode);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void showSizeDialog(final String mode) {
        final String[] sizes = {"3 x 3", "5 x 5", "7 x 7"};
        final int[] intSizes = {3, 5, 7};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose board size")
                .setItems(sizes, (dialog, which) -> {
                    int size = intSizes[which];
                    startGame(mode, size);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void startGame(String mode, int boardSize) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(Constants.EXTRA_MODE, mode);
        i.putExtra(Constants.EXTRA_BOARD_SIZE, boardSize);
        startActivity(i);
    }
}