package com.example.tic_toe_app.logic;

public class GameRules {
    public static Result checkResult(GameBoard board, int row, int col, char symbol, int winCondition) {
        if (checkWin(board, row, col, symbol, winCondition)) {
            return Result.WIN;
        }
        if (board.isFull()) {
            return Result.DRAW;
        }
        return Result.NONE;
    }
    private static boolean checkWin(GameBoard board, int row, int col, char symbol, int winCondition) {
        int[][] directions = {
                {0, 1},  // ngang
                {1, 0},  // dọc
                {1, 1},  // chéo xuống
                {1, -1}  // chéo lên
        };
        for (int[] d : directions) {
            int count = 1;
            count += countDirection(board, row, col, symbol, d[0], d[1]);
            count += countDirection(board, row, col, symbol, -d[0], -d[1]);
            if (count >= winCondition) return true;
        }
        return false;
    }
    private static int countDirection(GameBoard board, int row, int col, char symbol, int dr, int dc) {
        int count = 0;
        int r = row + dr, c = col + dc;

        while (r >= 0 && c >= 0 && r < board.getSize() && c < board.getSize()
                && board.getCell(r, c) == symbol) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }
}