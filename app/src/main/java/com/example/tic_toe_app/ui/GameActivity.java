package com.example.tic_toe_app.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tic_toe_app.R;
import com.example.tic_toe_app.utils.Constants;

public class GameActivity extends AppCompatActivity {

    private GridLayout boardGrid;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        boardGrid = findViewById(R.id.boardGrid);

        String mode = getIntent().getStringExtra(Constants.EXTRA_MODE);
        int boardSize = getIntent().getIntExtra(Constants.EXTRA_BOARD_SIZE, 3);


        TextView tvMode = findViewById(R.id.tvMode);
        TextView tvSize = findViewById(R.id.tvSize);

        tvMode.setText("Mode: " + (Constants.MODE_PVB.equals(mode) ? "PvBot" : "PvP"));
        tvSize.setText("Board: " + boardSize + " x " + boardSize);

        buildBoard(boardSize);

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetBoard());


        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void buildBoard(int size) {
        boardGrid.removeAllViews();
        boardGrid.setColumnCount(size);

        int total = size * size;
        int padding = dpToPx(16);
        int availableWidth = getResources().getDisplayMetrics().widthPixels - padding;
        int cellSize = availableWidth / size - dpToPx(8);

        for (int i = 0; i < total; i++) {
            final Button cell = new Button(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cellSize;
            lp.height = cellSize;
            lp.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
            cell.setLayoutParams(lp);
            cell.setText("");
            cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            cell.setAllCaps(false);
            cell.setGravity(Gravity.CENTER);
            cell.setBackgroundResource(R.drawable.button_background);

            final int idx = i;
            cell.setOnClickListener(v -> {
                // TODO: connect with game logic in Step 2
                Toast.makeText(GameActivity.this, "Cell " + idx + " clicked", Toast.LENGTH_SHORT).show();
            });

            boardGrid.addView(cell);
        }
    }
    private void resetBoard() {
        for (int i = 0; i < boardGrid.getChildCount(); i++) {
            View v = boardGrid.getChildAt(i);
            if (v instanceof Button) ((Button) v).setText("");
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}