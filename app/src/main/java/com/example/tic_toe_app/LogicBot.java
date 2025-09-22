package com.example.tic_toe_app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI Bot logic for Tic-Tac-Toe game
 * Supports Easy, Medium, and Hard difficulty levels
 */
public class LogicBot {
    
    private final int[][] board;
    private final int boardSize;
    private final String difficulty;
    
    public LogicBot(int[][] board, int boardSize, String difficulty) {
        this.board = board;
        this.boardSize = boardSize;
        this.difficulty = difficulty;
    }
    
    public int[] makeBotMove() {
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
        
        return move;
    }
    
    private int[] makeEasyMove() {
        List<int[]> emptyCells = getEmptyCells();
        if (!emptyCells.isEmpty()) {
            Random random = new Random();
            return emptyCells.get(random.nextInt(emptyCells.size()));
        }
        return null;
    }
    
    private int getWinLength() {
        if (boardSize == 3) return 3;
        if (boardSize == 5) return 4;
        return 5;
    }
    
    private int[] makeMediumMove() {
        int winLength = getWinLength();
        
        int[] blockMove = findBlockingMove(1, winLength);
        if (blockMove != null) {
            return blockMove;
        }
        
        return makeEasyMove();
    }
    
    private int[] makeHardMove() {
        int winLength = getWinLength();
        
        int[] winMove = findWinningMove(2, winLength);
        if (winMove != null) {
            return winMove;
        }
        
        int[] blockMove = findBlockingMove(1, winLength);
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
                board[row][col] = 0;
                return new int[]{row, col};
            }
            
            board[row][col] = 0;
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
    
    private boolean checkWinWithLength(int player, int winLength) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == player) {
                    if (checkDirection(player, row, col, 0, 1, winLength) ||
                        checkDirection(player, row, col, 1, 0, winLength) ||
                        checkDirection(player, row, col, 1, 1, winLength) ||
                        checkDirection(player, row, col, 1, -1, winLength)) {
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
}