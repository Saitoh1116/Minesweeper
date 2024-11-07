import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import msp.model.Cell;
import msp.model.GameState;
import msp.service.GameService;

class GameServiceTest {
    private GameService gameService;
    private static final int EXPECTED_MINES = 10;  // 期待される地雷の数
    private static final int BOARD_SIZE = 10;      // ボードサイズ

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    @RepeatedTest(10000) // 100回テストを繰り返す
    void testConsistentMineCount() {
        GameState game = gameService.newGame();
        
        // 1. 実際の地雷の数を数える
        int actualMines = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (game.getBoard()[i][j].isMine()) {
                    actualMines++;
                }
            }
        }
        
        // 2. 各チェック
        assertEquals(EXPECTED_MINES, actualMines, "実際の地雷の数が期待値と異なります");
        assertEquals(EXPECTED_MINES, game.getRemainingMines(), "残り地雷数の表示が実際の地雷数と異なります");
    }

    @Test
    void testAdjacentMinesCalculation() {
        GameState game = gameService.newGame();
        Cell[][] board = game.getBoard();
        
        // 各セルの隣接地雷数が正しく計算されているか確認
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (!board[i][j].isMine()) {
                    int calculatedCount = calculateAdjacentMines(board, i, j);
                    assertEquals(calculatedCount, board[i][j].getAdjacentMines(),
                        String.format("位置(%d,%d)の隣接地雷数が不正確です", i, j));
                }
            }
        }
    }

    @Test
    void testChainReactionCompleteness() {
        GameState game = gameService.newGame();
        Cell[][] board = game.getBoard();
        
        // 地雷のない空きセルを見つける
        int[] safeCell = findSafeCellWithNoAdjacentMines(board); // 修正: safeCellと誤記を統一
        if (safeCell != null) {
            int row = safeCell[0];
            int col = safeCell[1];
            
            // クリック前の状態をログ出力
            System.out.println("Clicking cell at: " + row + "," + col);
            System.out.println("Adjacent mines: " + board[row][col].getAdjacentMines());
            
            // セルをクリック
            gameService.handleClick(row, col, false);
            
            // 連鎖反応の確認
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    Cell cell = board[i][j];
                    if (!cell.isMine() && cell.getAdjacentMines() == 0) {
                        assertTrue(cell.isRevealed(),
                            String.format("空きセル(%d,%d)が開かれていません", i, j));
                        
                        // 周囲のセルのチェック
                        checkSurroundingCells(board, i, j);
                    }
                }
            }
        } else {
            fail("No safe cell with zero adjacent mines found");
        }
    }

    private int calculateAdjacentMines(Cell[][] board, int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < BOARD_SIZE && 
                    newCol >= 0 && newCol < BOARD_SIZE &&
                    board[newRow][newCol].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    private int[] findSafeCellWithNoAdjacentMines(Cell[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (!board[i][j].isMine() && board[i][j].getAdjacentMines() == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private void checkSurroundingCells(Cell[][] board, int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < BOARD_SIZE && 
                    newCol >= 0 && newCol < BOARD_SIZE &&
                    !board[newRow][newCol].isMine()) {
                    assertTrue(board[newRow][newCol].isRevealed(),
                        String.format("周囲のセル(%d,%d)が開かれていません", newRow, newCol));
                }
            }
        }
    }
}
