package com.example.tic_toe_app.logic;

public class GameBoard {
    private final int size;
    private final char[][] board;

    public GameBoard(int size) {
        this.size = size;
        this.board = new char[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = '-';
            }
        }
    }
    public int getSize() {
        return size;
    }
    public boolean placeMove(int row, int col, char symbol) {
        if (row < 0 || col < 0 || row >= size || col >= size) return false;
        if (board[row][col] != '-') return false;
        board[row][col] = symbol;
        return true;
    }
    public char getCell(int row, int col) {
        return board[row][col];
    }
    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '-') return false;
            }
        }
        return true;
    }
}