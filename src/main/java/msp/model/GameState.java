package msp.model;

import java.util.Random;

public class GameState {
    private Cell[][] board;
    private boolean gameOver;
    private boolean victory;
    private int remainingMines;
    private long timeElapsed;
    private int size;

    public GameState(int rows, int columns, int minesCount) {
        this.size = rows;
        this.remainingMines = minesCount;
        this.board = new Cell[rows][columns]; // 行数と列数をもとに初期化
        for(int i = 0; i<rows; i++) {
        	for(int j = 0; j <columns; j++) {
        		board[i][j] = new Cell();
            }
        }
        this.gameOver = false;
        this.victory = false;
    }

    public Cell[][] getBoard() { return board; }
    public boolean isGameOver() { return gameOver; }
    public boolean isVictory() { return victory; }
    public int getRemainingMines() { return remainingMines; }
    public long getTimeElapsed() { return timeElapsed; }

    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public void setVictory(boolean victory) { this.victory = victory; }
    public void setRemainingMines(int remainingMines) { this.remainingMines = remainingMines; }
    public void setTimeElapsed(long timeElapsed) { this.timeElapsed = timeElapsed; }

    // 地雷のランダム配置を公開メソッドに
    public void placeMines() {
        Random random = new Random();
        int placedMines = 0;
        while (placedMines < remainingMines) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            if (!board[x][y].isMine()) {
                board[x][y].setMine(true);
                placedMines++;
            }
        }
        calculateNumbers();
    }

    private void calculateNumbers() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!board[i][j].isMine()) {
                    board[i][j].setAdjacentMines(countAdjacentMines(i, j));
                }
            }
        }
    }

    private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                    if (board[newX][newY].isMine()) count++;
                }
            }
        }
        return count;
    }
}
