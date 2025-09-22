package com.example.tic_toe_app;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class GameActivity extends AppCompatActivity {
    private TextView tvGameInfo, tvScoreX, tvScoreTies, tvScoreO, tvOpponentLabel;
    private GridLayout gameBoard;
    private Button btnMainMenu, btnResetScore;
    private MusicManager musicManager;
    private int[][] board;
    private int boardSize = 3;
    private String gameMode = "PVP";
    private String difficulty = "Easy";
    private boolean isPlayerXTurn = true;
    private boolean gameEnded = false;
    private SharedPreferences prefs;
    private int scoreX = 0, scoreTies = 0, scoreO = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_game);
            musicManager = MusicManager.getInstance(this);
            if (getIntent() != null) {
                gameMode = getIntent().getStringExtra("GAME_MODE");
                String boardSizeStr = getIntent().getStringExtra("BOARD_SIZE");
                difficulty = getIntent().getStringExtra("DIFFICULTY");
                if (boardSizeStr != null && !boardSizeStr.isEmpty()) {
                    try {
                        boardSize = Integer.parseInt(boardSizeStr);
                    } catch (NumberFormatException e) {
                        boardSize = 3;
                    }
                }
                if (gameMode == null) gameMode = "PVP";
                if (difficulty == null) difficulty = "Easy";
            }
            prefs = getSharedPreferences("TicToeScores", MODE_PRIVATE);
            initializeViews();
            setupGameInfo();
            loadScores();
            initializeBoard();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                setContentView(R.layout.activity_game);
                Toast.makeText(this, "Error starting game: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception fallbackError) {
                finish();
            }
        }
    }
    private void initializeViews() {
        try {
            tvGameInfo = findViewById(R.id.tvGameInfo);
            tvScoreX = findViewById(R.id.tvScoreX);
            tvScoreTies = findViewById(R.id.tvScoreTies);
            tvScoreO = findViewById(R.id.tvScoreO);
            tvOpponentLabel = findViewById(R.id.tvOpponentLabel);
            gameBoard = findViewById(R.id.gameBoard);
            btnMainMenu = findViewById(R.id.btnMainMenu);
            btnResetScore = findViewById(R.id.btnResetScore);
            if (gameBoard == null) {
                throw new RuntimeException("GameBoard not found in layout");
            }
            if (btnMainMenu != null) {
                btnMainMenu.setOnClickListener(v -> showMainMenuConfirmDialog());
            }
            if (btnResetScore != null) {
                btnResetScore.setOnClickListener(v -> {
                    scoreX = 0;
                    scoreTies = 0;
                    scoreO = 0;
                    saveScores();
                    updateScoreDisplay();
                    Toast.makeText(this, "Scores reset!", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            throw e;
        }
    }
    private void setupGameInfo() {
        try {
            String info;
            if ("PVBOT".equals(gameMode)) {
                info = "Mode: 1P  Size: " + boardSize + "x" + boardSize + "  Difficulty: " + difficulty;
                if (tvOpponentLabel != null) tvOpponentLabel.setText("O (AI)");
                if (btnResetScore != null) btnResetScore.setVisibility(View.GONE);
            } else {
                info = "Mode: 2P  Size: " + boardSize + "x" + boardSize;
                if (tvOpponentLabel != null) tvOpponentLabel.setText("O (P2)");
                if (btnResetScore != null) btnResetScore.setVisibility(View.VISIBLE);
            }
            if (tvGameInfo != null) tvGameInfo.setText(info);
        } catch (Exception e) {
        } catch (Exception e) {
        }
    }
    private void loadScores() {
        try {
            String key = gameMode + "_" + boardSize + "x" + boardSize;
            scoreX = prefs.getInt(key + "_X", 0);
            scoreTies = prefs.getInt(key + "_TIES", 0);
            scoreO = prefs.getInt(key + "_O", 0);
            updateScoreDisplay();
        } catch (Exception e) {
        }
    }
    private void saveScores() {
        try {
            String key = gameMode + "_" + boardSize + "x" + boardSize;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(key + "_X", scoreX);
            editor.putInt(key + "_TIES", scoreTies);
            editor.putInt(key + "_O", scoreO);
            editor.apply();
        } catch (Exception e) {
        }
    }
    private void updateScoreDisplay() {
        try {
            if (tvScoreX != null) tvScoreX.setText(String.valueOf(scoreX));
            if (tvScoreTies != null) tvScoreTies.setText(String.valueOf(scoreTies));
            if (tvScoreO != null) tvScoreO.setText(String.valueOf(scoreO));
        } catch (Exception e) {
        }
    }
    private void initializeBoard() {
        try {
            if (boardSize <= 0 || boardSize > 10) {
                boardSize = 3;
            }
            board = new int[boardSize][boardSize];
            if (gameBoard == null) {
                return;
            }
            gameBoard.removeAllViews();
            gameBoard.setColumnCount(boardSize);
            gameBoard.setRowCount(boardSize);
            int cellSize;
            int finalGapSize;
            try {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;
                float density = displayMetrics.density;
                int reservedHeight = Math.round((120 + 100 + 80 + 60) * density);
                int horizontalPadding = Math.round(60 * density);
                int availableWidth = screenWidth - horizontalPadding;
                int availableHeight = screenHeight - reservedHeight;
                int maxBoardDimension = Math.min(availableWidth, availableHeight);
                finalGapSize = 0;
                cellSize = maxBoardDimension / boardSize;
                int minCellSize = Math.round(45 * density);
                int maxCellSize = Math.round(100 * density);
                if (cellSize < minCellSize) {
                    cellSize = minCellSize;
                } else if (cellSize > maxCellSize) {
                    cellSize = maxCellSize;
                }
            } catch (Exception scalingError) {
                scalingError.printStackTrace();
                cellSize = 150;
                finalGapSize = 4;
            }
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    ImageView cell = new ImageView(this);
                    if (cell == null) {
                        continue;
                    }
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = cellSize;
                    params.height = cellSize;
                    int margin1dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1, getResources().getDisplayMetrics());
                    params.setMargins(
                        j == 0 ? 0 : margin1dp,
                        i == 0 ? 0 : margin1dp,
                        0,
                        0
                    );
                    params.columnSpec = GridLayout.spec(j);
                    params.rowSpec = GridLayout.spec(i);
                    cell.setLayoutParams(params);
                    try {
                        cell.setBackgroundResource(R.drawable.game_cell_selector);
                        cell.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        int padding = Math.max(2, cellSize * 10 / 100);
                        cell.setPadding(padding, padding, padding, padding);
                        final int row = i;
                        final int col = j;
                        cell.setOnClickListener(v -> onCellClicked(row, col, cell));
                        gameBoard.addView(cell);
                        board[i][j] = 0;
                    } catch (Exception cellException) {
                        cellException.printStackTrace();
                    }
                }
            }
            isPlayerXTurn = true;
            gameEnded = false;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            try {
                boardSize = 3;
                board = new int[3][3];
            } catch (Exception fallbackError) {
            }
        }
    }
    private void onCellClicked(int row, int col, ImageView cell) {
        try {
            if (gameEnded || board[row][col] != 0) {
                return;
            }
            if (isPlayerXTurn) {
                board[row][col] = 1;
                cell.setImageResource(R.drawable.x_pixel);
            } else {
                board[row][col] = 2;
                cell.setImageResource(R.drawable.o_pixel);
            }
            if (checkWin()) {
                String winner = isPlayerXTurn ? "X" : "O";
                gameEnded = true;
                if (isPlayerXTurn) scoreX++;
                else scoreO++;
                if ("PVBOT".equals(gameMode)) {
                    boolean playerWon = isPlayerXTurn; // X is always player, O is bot
                    AchievementsActivity.updateGameStats(this, playerWon, difficulty);
                    int[] stats = AchievementsActivity.getCurrentStats(this);
                    int totalWins = stats[0];
                    int totalLosses = stats[1]; 
                    if (playerWon && (totalWins == 10 || totalWins == 25 || totalWins == 50 || totalWins == 100)) {
                        showAchievementToast("🏆 Achievement Milestone: " + totalWins + " wins vs AI!");
                    } else if (!playerWon && (totalLosses == 10 || totalLosses == 25 || totalLosses == 50 || totalLosses == 100)) {
                        showAchievementToast("💪 Never Give Up: " + totalLosses + " battles fought!");
                    }
                }
                saveScores();
                updateScoreDisplay();
                showGameEndDialog(winner + " Wins!");
            } else if (isBoardFull()) {
                gameEnded = true;
                scoreTies++;
                if ("PVBOT".equals(gameMode)) {
                }
                saveScores();
                updateScoreDisplay();
                showGameEndDialog("It's a Tie!");
            } else {
                isPlayerXTurn = !isPlayerXTurn;
                if (!isPlayerXTurn && "PVBOT".equals(gameMode) && !gameEnded) {
                    makeBotMove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean checkWin() {
        int player = isPlayerXTurn ? 1 : 2;
        if (boardSize == 3) {
            return checkWinWithLength(player, 3);
        } else if (boardSize == 5) {
            return checkWinWithLength(player, 4);
        } else {
            return checkWinWithLength(player, 5);
        }
    }
    private boolean checkWinWithLength(int player, int winLength) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == player) {
                    if (checkDirection(player, row, col, 0, 1, winLength) ||  // Horizontal
                        checkDirection(player, row, col, 1, 0, winLength) ||  // Vertical
                        checkDirection(player, row, col, 1, 1, winLength) ||  // Diagonal \
                        checkDirection(player, row, col, 1, -1, winLength)) { // Diagonal /
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean checkDirection(int player, int startRow, int startCol, int deltaRow, int deltaCol, int winLength) {
        int count = 0;
        int row = startRow;
        int col = startCol;
        while (row >= 0 && row < boardSize && col >= 0 && col < boardSize && 
               board[row][col] == player && count < winLength) {
            count++;
            row += deltaRow;
            col += deltaCol;
        }
        return count >= winLength;
    }
    private boolean isBoardFull() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    private void makeBotMove() {
        int[] move = null;
        switch (difficulty.toLowerCase()) {
            case "easy":
                move = makeEasyMove();
                break;
            case "medium":
                move = makeMediumMove();
                break;
            case "hard":
                move = makeHardMove();
                break;
            default:
                move = makeEasyMove();
                break;
        }
        if (move != null) {
            int position = move[0] * boardSize + move[1];
            if (position < gameBoard.getChildCount()) {
                ImageView cell = (ImageView) gameBoard.getChildAt(position);
                onCellClicked(move[0], move[1], cell);
            }
        }
    }
    private int[] makeEasyMove() {
        List<int[]> emptyCells = getEmptyCells();
        if (!emptyCells.isEmpty()) {
            Random random = new Random();
            return emptyCells.get(random.nextInt(emptyCells.size()));
        }
        return null;
    }
    private int[] makeMediumMove() {
        int winLength;
        if (boardSize == 3) {
            winLength = 3;
        } else if (boardSize == 5) {
            winLength = 4;
        } else {
            winLength = 5; // For 7x7
        }
        int[] blockMove = findBlockingMove(1, winLength); // Player is 1
        if (blockMove != null) {
            return blockMove;
        }
        return makeEasyMove();
    }
    private int[] makeHardMove() {
        int winLength;
        if (boardSize == 3) {
            winLength = 3;
        } else if (boardSize == 5) {
            winLength = 4;
        } else {
            winLength = 5; // For 7x7
        }
        int[] winMove = findWinningMove(2, winLength); // Bot is 2
        if (winMove != null) {
            return winMove;
        }
        int[] blockMove = findBlockingMove(1, winLength); // Player is 1
        if (blockMove != null) {
            return blockMove;
        }
        int[] strategicMove = findStrategicMove();
        if (strategicMove != null) {
            return strategicMove;
        }
        return makeEasyMove();
    }
    private List<int[]> getEmptyCells() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells;
    }
    private int[] findWinningMove(int player, int winLength) {
        List<int[]> emptyCells = getEmptyCells();
        for (int[] cell : emptyCells) {
            int row = cell[0];
            int col = cell[1];
            board[row][col] = player;
            if (checkWinWithLength(player, winLength)) {
                board[row][col] = 0; // Restore
                return new int[]{row, col};
            }
            board[row][col] = 0; // Restore
        }
        return null;
    }
    private int[] findBlockingMove(int opponentPlayer, int winLength) {
        return findWinningMove(opponentPlayer, winLength);
    }
    private int[] findStrategicMove() {
        List<int[]> emptyCells = getEmptyCells();
        if (emptyCells.isEmpty()) return null;
        if (boardSize == 3) {
            if (board[1][1] == 0) return new int[]{1, 1};
            int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
            for (int[] corner : corners) {
                if (board[corner[0]][corner[1]] == 0) {
                    return corner;
                }
            }
        } else {
            int center = boardSize / 2;
            int range = Math.max(1, boardSize / 4);
            for (int r = Math.max(0, center - range); r <= Math.min(boardSize - 1, center + range); r++) {
                for (int c = Math.max(0, center - range); c <= Math.min(boardSize - 1, center + range); c++) {
                    if (board[r][c] == 0) {
                        return new int[]{r, c};
                    }
                }
            }
        }
        Random random = new Random();
        return emptyCells.get(random.nextInt(emptyCells.size()));
    }
    private void showGameEndDialog(String message) {
        try {
            String title;
            String detailedMessage;
            if (message.contains("Wins")) {
                if (message.contains("X")) {
                    title = "Victory!";
                    if ("PVBOT".equals(gameMode)) {
                        detailedMessage = "Congratulations! You defeated the bot AI!\n";
                    } else {
                        detailedMessage = "Player X wins this round!\n";
                    }
                } else {
                    title = "Defeat";
                    if ("PVBOT".equals(gameMode)) {
                        detailedMessage = "The AI wins this time. Better luck next round!\n";
                    } else {
                        detailedMessage = "Player O wins this round!\n";
                    }
                }
            } else { // Tie
                title = "Draw";
                detailedMessage = "It's a tie! Well played by both sides.\n";
            }
            new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                    .setTitle(title)
                    .setMessage(detailedMessage)
                    .setPositiveButton("New Game", (dialog, which) -> {
                        initializeBoard();
                    })
                    .setNegativeButton("Main Menu", (dialog, which) -> {
                        showMainMenuConfirmDialog();
                    })
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, message + "\nScore: " + scoreX + " - " + scoreO, Toast.LENGTH_LONG).show();
        }
    }
    private void showMainMenuConfirmDialog() {
        try {
            new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                    .setTitle("Return to Main Menu")
                    .setMessage("Are you sure you want to return to the main menu?\n\nThe current game session will be saved, but your game progress will be lost.")
                    .setPositiveButton("Exit", (dialog, which) -> {
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .setCancelable(true)
                    .show();
        } catch (Exception e) {
            finish();
        }
    }
    private void showAchievementToast(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }
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
}
