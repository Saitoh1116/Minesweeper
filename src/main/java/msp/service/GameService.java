package msp.service;

import org.springframework.stereotype.Service;

import msp.model.Cell;
import msp.model.GameState;

@Service
public class GameService {
    private static final int BOARD_SIZE = 10; // 9x9のボードに修正
    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLUMNS = 10;
    private static final int MINES_COUNT = 10;
    private GameState currentGame;
    private long startTime;
    
    public GameState newGame() {
        currentGame = new GameState(BOARD_ROWS, BOARD_COLUMNS, MINES_COUNT);
        currentGame.placeMines();
        startTime = System.currentTimeMillis();
        return currentGame;
    }
    
    public GameService() {
        this.currentGame = new GameState(BOARD_ROWS, BOARD_COLUMNS, MINES_COUNT);
        currentGame.placeMines();
        startTime = System.currentTimeMillis();
    }

    public GameState getCurrentState() {
        if (currentGame != null && !currentGame.isGameOver()) {
            currentGame.setTimeElapsed((System.currentTimeMillis() - startTime) / 1000);
        }
        return currentGame;
    }
    
    public GameState handleClick(int row, int col, boolean isFlag) {
        if (currentGame.isGameOver()) {
            return currentGame;
        }

        if (isFlag) {
            toggleFlag(row, col);
            checkVictory(); // フラグを立てた後に勝利判定
            return currentGame;
        }
        
        Cell cell = currentGame.getBoard()[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return currentGame;
        }

        revealCell(row, col);
        return currentGame;
    }
    
    private void toggleFlag(int row, int col) {
        Cell cell = currentGame.getBoard()[row][col];
        
        if (!cell.isRevealed()) {
            boolean newFlagState = !cell.isFlagged();
            cell.setFlagged(newFlagState);
            // フラグの数に応じて残り地雷数を更新
            currentGame.setRemainingMines(currentGame.getRemainingMines() + (newFlagState ? -1 : 1));
        }
    }

    private void revealCell(int row, int col) {
        Cell cell = currentGame.getBoard()[row][col];
        
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }
        
        cell.setRevealed(true);
        
        if (cell.isMine()) {
            gameOver(false);
            return;
        }
        
        if (cell.getAdjacentMines() == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newRow = row + i;
                    int newCol = col + j;
                    if (isValidPosition(newRow, newCol)) {
                        revealCell(newRow, newCol);
                    }
                }
            }
        }
        checkVictory();
    }
    
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_ROWS && col >= 0 && col < BOARD_COLUMNS;
    }

    private void checkVictory() {
        // すべての地雷にフラグが立っているか、
        // または地雷以外のすべてのセルが開かれているかをチェック
        int correctFlags = 0;
        int revealedNonMines = 0;
        int totalNonMines = (BOARD_ROWS * BOARD_COLUMNS) - MINES_COUNT;
        
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                Cell cell = currentGame.getBoard()[i][j];
                if (cell.isMine() && cell.isFlagged()) {
                    correctFlags++;
                }
                if (!cell.isMine() && cell.isRevealed()) {
                    revealedNonMines++;
                }
            }
        }

        if (correctFlags == MINES_COUNT || revealedNonMines == totalNonMines) {
            gameOver(true);
        }
    }

    private void gameOver(boolean victory) {
        currentGame.setGameOver(true);
        currentGame.setVictory(victory);
        
        if (!victory) {
            // 地雷をすべて表示
            for (int i = 0; i < BOARD_ROWS; i++) {
                for (int j = 0; j < BOARD_COLUMNS; j++) {
                    Cell cell = currentGame.getBoard()[i][j];
                    if (cell.isMine()) {
                        cell.setRevealed(true);
                    }
                }
            }
        }
    }
}