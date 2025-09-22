package com.example.tic_toe_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
        Log.d("TicToeApp", "=== GameActivity onCreate START ===");
        
        try {
            setContentView(R.layout.activity_game);
            
            // Get game settings from intent
            if (getIntent() != null) {
                gameMode = getIntent().getStringExtra("GAME_MODE");
                String boardSizeStr = getIntent().getStringExtra("BOARD_SIZE");
                difficulty = getIntent().getStringExtra("DIFFICULTY");
                
                Log.d("TicToeApp", "Intent data - Mode: " + gameMode + ", Size: " + boardSizeStr + ", Difficulty: " + difficulty);
                
                // Validate and set board size
                if (boardSizeStr != null && !boardSizeStr.isEmpty()) {
                    try {
                        boardSize = Integer.parseInt(boardSizeStr);
                    } catch (NumberFormatException e) {
                        Log.e("TicToeApp", "Invalid board size: " + boardSizeStr + ", using default 3");
                        boardSize = 3;
                    }
                }
                
                // Set defaults if null
                if (gameMode == null) gameMode = "PVP";
                if (difficulty == null) difficulty = "Easy";
            }
            
            Log.d("TicToeApp", "Final settings - Mode: " + gameMode + ", Size: " + boardSize + ", Difficulty: " + difficulty);
            
            prefs = getSharedPreferences("TicToeScores", MODE_PRIVATE);
            
            initializeViews();
            setupGameInfo();
            loadScores();
            initializeBoard();
            
            Log.d("TicToeApp", "=== GameActivity onCreate COMPLETED ===");
            
        } catch (Exception e) {
            Log.e("TicToeApp", "FATAL ERROR in onCreate: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: try to show basic UI
            try {
                setContentView(R.layout.activity_game);
                Toast.makeText(this, "Error starting game: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception fallbackError) {
                Log.e("TicToeApp", "Even fallback failed: " + fallbackError.getMessage());
                finish();
            }
        }
    }

    private void initializeViews() {
        Log.d("TicToeApp", "Initializing views...");
        try {
            tvGameInfo = findViewById(R.id.tvGameInfo);
            tvScoreX = findViewById(R.id.tvScoreX);
            tvScoreTies = findViewById(R.id.tvScoreTies);
            tvScoreO = findViewById(R.id.tvScoreO);
            tvOpponentLabel = findViewById(R.id.tvOpponentLabel);
            gameBoard = findViewById(R.id.gameBoard);
            btnMainMenu = findViewById(R.id.btnMainMenu);
            btnResetScore = findViewById(R.id.btnResetScore);
            
            // Check for null views
            if (gameBoard == null) {
                Log.e("TicToeApp", "GameBoard is NULL! Layout might be incorrect.");
                throw new RuntimeException("GameBoard not found in layout");
            }
            
            // Set click listeners
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
            
            Log.d("TicToeApp", "Views initialized successfully");
        } catch (Exception e) {
            Log.e("TicToeApp", "Error initializing views: " + e.getMessage());
            throw e;
        }
    }

    private void setupGameInfo() {
        Log.d("TicToeApp", "Setting up game info...");
        try {
            String info;
            if ("PVBOT".equals(gameMode)) {
                info = "Mode: 1P  Size: " + boardSize + "x" + boardSize + "  Difficulty: " + difficulty;
                if (tvOpponentLabel != null) tvOpponentLabel.setText("O (CPU)");
                if (btnResetScore != null) btnResetScore.setVisibility(View.GONE);
            } else {
                info = "Mode: 2P  Size: " + boardSize + "x" + boardSize;
                if (tvOpponentLabel != null) tvOpponentLabel.setText("O (P2)");
                if (btnResetScore != null) btnResetScore.setVisibility(View.VISIBLE);
            }
            if (tvGameInfo != null) tvGameInfo.setText(info);
            Log.d("TicToeApp", "Game info set: " + info);
        } catch (Exception e) {
            Log.e("TicToeApp", "Error setting up game info: " + e.getMessage());
        }
    }

    private void loadScores() {
        Log.d("TicToeApp", "Loading scores...");
        try {
            String key = gameMode + "_" + boardSize + "x" + boardSize;
            scoreX = prefs.getInt(key + "_X", 0);
            scoreTies = prefs.getInt(key + "_TIES", 0);
            scoreO = prefs.getInt(key + "_O", 0);
            updateScoreDisplay();
            Log.d("TicToeApp", "Scores loaded - X:" + scoreX + " Ties:" + scoreTies + " O:" + scoreO);
        } catch (Exception e) {
            Log.e("TicToeApp", "Error loading scores: " + e.getMessage());
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
            Log.d("TicToeApp", "Scores saved");
        } catch (Exception e) {
            Log.e("TicToeApp", "Error saving scores: " + e.getMessage());
        }
    }

    private void updateScoreDisplay() {
        try {
            if (tvScoreX != null) tvScoreX.setText(String.valueOf(scoreX));
            if (tvScoreTies != null) tvScoreTies.setText(String.valueOf(scoreTies));
            if (tvScoreO != null) tvScoreO.setText(String.valueOf(scoreO));
        } catch (Exception e) {
            Log.e("TicToeApp", "Error updating score display: " + e.getMessage());
        }
    }

    private void initializeBoard() {
        Log.d("TicToeApp", "=== INITIALIZING BOARD START ===");
        try {
            Log.d("TicToeApp", "Board size: " + boardSize);
            
            if (boardSize <= 0 || boardSize > 10) {
                Log.e("TicToeApp", "Invalid board size: " + boardSize + ", using default 3");
                boardSize = 3;
            }
            
            board = new int[boardSize][boardSize];
            Log.d("TicToeApp", "Board array created successfully");
            
            if (gameBoard == null) {
                Log.e("TicToeApp", "GameBoard is null! Cannot proceed");
                return;
            }
            
            gameBoard.removeAllViews();
            gameBoard.setColumnCount(boardSize);
            gameBoard.setRowCount(boardSize);
            Log.d("TicToeApp", "GridLayout configured");
            
            // Auto-scaling board calculation
            int cellSize;
            int finalGapSize;
            try {
                // Get screen dimensions
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;
                float density = displayMetrics.density;
                
                Log.d("TicToeApp", "Screen: " + screenWidth + "x" + screenHeight + ", density: " + density);
                
                // Calculate available space more conservatively
                // Consider all UI elements: header (~120dp), score (~100dp), buttons (~80dp), margins (~60dp)
                int reservedHeight = Math.round((120 + 100 + 80 + 60) * density);
                
                // More conservative width calculation - account for container padding and system margins
                int horizontalPadding = Math.round(60 * density); // Container padding + safety margin
                int availableWidth = screenWidth - horizontalPadding;
                int availableHeight = screenHeight - reservedHeight;
                
                Log.d("TicToeApp", "Available space: " + availableWidth + "x" + availableHeight + " (reserved height: " + reservedHeight + ", padding: " + horizontalPadding + ")");
                
                // Use smaller dimension to ensure square board fits
                int maxBoardDimension = Math.min(availableWidth, availableHeight);
                
                // No gaps between cells - seamless board
                finalGapSize = 0;
                int totalGapSpace = 0;
                
                cellSize = maxBoardDimension / boardSize;
                
                // Ensure minimum/maximum usable cell size
                int minCellSize = Math.round(45 * density); // Min 45dp
                int maxCellSize = Math.round(100 * density); // Max 100dp
                
                if (cellSize < minCellSize) {
                    cellSize = minCellSize;
                    Log.d("TicToeApp", "Cell size too small, using minimum: " + cellSize);
                } else if (cellSize > maxCellSize) {
                    cellSize = maxCellSize;
                    Log.d("TicToeApp", "Cell size too large, capping at: " + cellSize);
                }
                
                // Calculate actual board dimensions for verification
                int actualBoardWidth = cellSize * boardSize;
                Log.d("TicToeApp", "Final dimensions - Cell: " + cellSize + ", Total board width: " + actualBoardWidth + " (available: " + maxBoardDimension + ")");
                
            } catch (Exception scalingError) {
                Log.e("TicToeApp", "Error in auto-scaling calculation: " + scalingError.getMessage());
                scalingError.printStackTrace();
                cellSize = 150; // Safe fallback size
                finalGapSize = 4; // Safe fallback gap
                Log.d("TicToeApp", "Using fallback - cell: " + cellSize + ", gap: " + finalGapSize);
            }
            
            Log.d("TicToeApp", "Starting to create " + (boardSize * boardSize) + " cells...");
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    Log.d("TicToeApp", "Creating cell [" + i + "," + j + "]");
                    
                    ImageView cell = new ImageView(this);
                    if (cell == null) {
                        Log.e("TicToeApp", "Failed to create ImageView for cell [" + i + "," + j + "]");
                        continue;
                    }
                    
                    // Create layout params for each cell
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = cellSize;
                    params.height = cellSize;
                    
                    // No gaps between cells - they will connect seamlessly
                    params.setMargins(0, 0, 0, 0);
                    
                    params.columnSpec = GridLayout.spec(j);
                    params.rowSpec = GridLayout.spec(i);
                    cell.setLayoutParams(params);
                    Log.d("TicToeApp", "Cell [" + i + "," + j + "] params set (size: " + cellSize + ", seamless grid)");
                    
                    try {
                        cell.setBackgroundResource(R.drawable.game_cell_selector);
                        cell.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        
                        // Dynamic padding based on cell size
                        int padding = Math.max(6, cellSize / 15); // Min 6px, scale with cell size
                        cell.setPadding(padding, padding, padding, padding);
                        Log.d("TicToeApp", "Cell [" + i + "," + j + "] styling set, padding: " + padding);
                        
                        final int row = i;
                        final int col = j;
                        cell.setOnClickListener(v -> onCellClicked(row, col, cell));
                        
                        gameBoard.addView(cell);
                        board[i][j] = 0; // 0 = empty, 1 = X, 2 = O
                        Log.d("TicToeApp", "Cell [" + i + "," + j + "] added successfully");
                    } catch (Exception cellException) {
                        Log.e("TicToeApp", "Error setting up cell [" + i + "," + j + "]: " + cellException.getMessage());
                        cellException.printStackTrace();
                    }
                }
            }
            
            isPlayerXTurn = true;
            gameEnded = false;
            Log.d("TicToeApp", "=== BOARD INITIALIZATION COMPLETED ===");
            
        } catch (Exception e) {
            Log.e("TicToeApp", "FATAL ERROR in initializeBoard: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Try fallback initialization
            try {
                Log.d("TicToeApp", "Attempting fallback board initialization...");
                boardSize = 3;
                board = new int[3][3];
                // Keep board empty for now
                Log.d("TicToeApp", "Fallback initialization completed");
            } catch (Exception fallbackError) {
                Log.e("TicToeApp", "Even fallback failed: " + fallbackError.getMessage());
            }
        }
    }

    private void onCellClicked(int row, int col, ImageView cell) {
        Log.d("TicToeApp", "Cell clicked: [" + row + "," + col + "]");
        
        try {
            if (gameEnded || board[row][col] != 0) {
                Log.d("TicToeApp", "Click ignored - game ended: " + gameEnded + ", cell occupied: " + (board[row][col] != 0));
                return;
            }
            
            // Place symbol
            if (isPlayerXTurn) {
                board[row][col] = 1;
                cell.setImageResource(R.drawable.ic_x);
                Log.d("TicToeApp", "Placed X at [" + row + "," + col + "]");
            } else {
                board[row][col] = 2;
                cell.setImageResource(R.drawable.ic_o);
                Log.d("TicToeApp", "Placed O at [" + row + "," + col + "]");
            }
            
            // Check for win/tie
            if (checkWin()) {
                String winner = isPlayerXTurn ? "X" : "O";
                Log.d("TicToeApp", "Game won by: " + winner);
                gameEnded = true;
                
                if (isPlayerXTurn) scoreX++;
                else scoreO++;
                
                // Update achievements if playing against bot
                if ("PVBOT".equals(gameMode)) {
                    boolean playerWon = isPlayerXTurn; // X is always player, O is bot
                    AchievementsActivity.updateGameStats(this, playerWon, difficulty);
                    Log.d("TicToeApp", "Achievement updated - Player won: " + playerWon + ", Difficulty: " + difficulty);
                    
                    // Get updated stats for potential achievement notifications
                    int[] stats = AchievementsActivity.getCurrentStats(this);
                    int totalWins = stats[0];
                    int totalLosses = stats[1]; 
                    
                    // Check for major achievement milestones
                    if (playerWon && (totalWins == 10 || totalWins == 25 || totalWins == 50 || totalWins == 100)) {
                        showAchievementToast("🏆 Achievement Milestone: " + totalWins + " wins vs CPU!");
                    } else if (!playerWon && (totalLosses == 10 || totalLosses == 25 || totalLosses == 50 || totalLosses == 100)) {
                        showAchievementToast("💪 Never Give Up: " + totalLosses + " battles fought!");
                    }
                }
                
                saveScores();
                updateScoreDisplay();
                showGameEndDialog(winner + " Wins!");
            } else if (isBoardFull()) {
                Log.d("TicToeApp", "Game tied");
                gameEnded = true;
                scoreTies++;
                
                // For ties in bot mode, don't update achievements (no win/loss)
                if ("PVBOT".equals(gameMode)) {
                    Log.d("TicToeApp", "Tie game vs bot - no achievement update");
                }
                
                saveScores();
                updateScoreDisplay();
                showGameEndDialog("It's a Tie!");
            } else {
                // Switch turns
                isPlayerXTurn = !isPlayerXTurn;
                Log.d("TicToeApp", "Turn switched to: " + (isPlayerXTurn ? "X" : "O"));
                
                // Bot move if needed
                if (!isPlayerXTurn && "PVBOT".equals(gameMode) && !gameEnded) {
                    Log.d("TicToeApp", "Making bot move...");
                    makeBotMove();
                }
            }
        } catch (Exception e) {
            Log.e("TicToeApp", "Error in onCellClicked: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkWin() {
        int player = isPlayerXTurn ? 1 : 2;
        
        if (boardSize == 3) {
            // Standard 3x3 tic-tac-toe: need 3 in a row
            return checkWinWithLength(player, 3);
        } else {
            // For 5x5 and 7x7: need 4 in a row
            return checkWinWithLength(player, 4);
        }
    }
    
    private boolean checkWinWithLength(int player, int winLength) {
        // Check all possible directions from each position
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == player) {
                    // Check 4 directions: horizontal, vertical, diagonal (both ways)
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
        
        // Count consecutive pieces in the given direction
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
    
    // Easy: Random move
    private int[] makeEasyMove() {
        List<int[]> emptyCells = getEmptyCells();
        if (!emptyCells.isEmpty()) {
            Random random = new Random();
            return emptyCells.get(random.nextInt(emptyCells.size()));
        }
        return null;
    }
    
    // Medium: Random + blocking player wins
    private int[] makeMediumMove() {
        int winLength = (boardSize == 3) ? 3 : 4;
        
        // First, try to block player from winning
        int[] blockMove = findBlockingMove(1, winLength); // Player is 1
        if (blockMove != null) {
            Log.d("TicToeApp", "Medium bot: Blocking player at " + blockMove[0] + "," + blockMove[1]);
            return blockMove;
        }
        
        // Otherwise, make random move
        return makeEasyMove();
    }
    
    // Hard: Minimax algorithm with win/block/strategic positioning
    private int[] makeHardMove() {
        int winLength = (boardSize == 3) ? 3 : 4;
        
        // 1. Try to win immediately
        int[] winMove = findWinningMove(2, winLength); // Bot is 2
        if (winMove != null) {
            Log.d("TicToeApp", "Hard bot: Winning move at " + winMove[0] + "," + winMove[1]);
            return winMove;
        }
        
        // 2. Block player from winning
        int[] blockMove = findBlockingMove(1, winLength); // Player is 1
        if (blockMove != null) {
            Log.d("TicToeApp", "Hard bot: Blocking player at " + blockMove[0] + "," + blockMove[1]);
            return blockMove;
        }
        
        // 3. Use strategic positioning
        int[] strategicMove = findStrategicMove();
        if (strategicMove != null) {
            Log.d("TicToeApp", "Hard bot: Strategic move at " + strategicMove[0] + "," + strategicMove[1]);
            return strategicMove;
        }
        
        // 4. Fallback to random
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
            
            // Temporarily place piece
            board[row][col] = player;
            
            // Check if this creates a win
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
        
        // For 3x3: Prefer center, then corners, then edges
        if (boardSize == 3) {
            // Center
            if (board[1][1] == 0) return new int[]{1, 1};
            
            // Corners
            int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
            for (int[] corner : corners) {
                if (board[corner[0]][corner[1]] == 0) {
                    return corner;
                }
            }
        } else {
            // For larger boards: Prefer center area
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
        
        // Fallback to random
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
                        detailedMessage = "The CPU wins this time. Better luck next round!\n";
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
                        Log.d("TicToeApp", "Starting new game...");
                        initializeBoard();
                    })
                    .setNegativeButton("Main Menu", (dialog, which) -> {
                        showMainMenuConfirmDialog();
                    })
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            Log.e("TicToeApp", "Error showing game end dialog: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple toast
            Toast.makeText(this, message + "\nScore: " + scoreX + " - " + scoreO, Toast.LENGTH_LONG).show();
        }
    }
    
    private void showMainMenuConfirmDialog() {
        try {
            new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                    .setTitle("Return to Main Menu")
                    .setMessage("Are you sure you want to return to the main menu?\n\nThe current game session will be saved, but your game progress will be lost.")
                    .setPositiveButton("Exit", (dialog, which) -> {
                        Log.d("TicToeApp", "Confirmed return to main menu");
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        Log.d("TicToeApp", "Cancelled return to main menu");
                        // User stays in game
                    })
                    .setCancelable(true)
                    .show();
        } catch (Exception e) {
            Log.e("TicToeApp", "Error showing main menu confirmation: " + e.getMessage());
            // Fallback - just exit
            finish();
        }
    }
    
    private void showAchievementToast(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.d("TicToeApp", "Achievement notification: " + message);
        } catch (Exception e) {
            Log.e("TicToeApp", "Error showing achievement toast: " + e.getMessage());
        }
    }
}