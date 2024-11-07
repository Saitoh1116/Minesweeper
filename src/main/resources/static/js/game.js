// ゲームの状態管理
let gameState = {
    board: [],
    gameOver: false,
    victory: false,
    remainingMines: 10,
    timeStart: null,
    firstMove: true
};

let timerInterval;

// 初期化処理
async function initGame() {
    try {
        const response = await fetch('/api/newGame', {
            method: 'POST'
        });
        gameState = await response.json();
        createBoard();
        startTimer();
        updateMineCount(); // 地雷カウントの初期表示
    } catch (error) {
        console.error('Error initializing game:', error);
    }
}

// ボード作成
function createBoard() {
    const boardElement = document.getElementById('board');
    boardElement.innerHTML = '';
    
    // ボード全体の右クリック防止
    boardElement.addEventListener('contextmenu', (e) => {
        e.preventDefault();
        return false;
    });

    for (let i = 0; i < 9; i++) {
        const row = document.createElement('div');
        row.className = 'board-row';
        
        for (let j = 0; j < 9; j++) {
            const cell = document.createElement('div');
            cell.className = 'cell';
            cell.dataset.row = i;
            cell.dataset.col = j;
            
            // 左クリックイベント
            cell.addEventListener('click', (e) => {
                e.preventDefault();
                handleClick(i, j, false);
            });
            
            // 右クリックイベント
            cell.addEventListener('contextmenu', (e) => {
                e.preventDefault();
                handleClick(i, j, true);
                return false;
            });
            
            // 右クリック長押し（モバイル対応）
            let touchTimeout;
            cell.addEventListener('touchstart', (e) => {
                touchTimeout = setTimeout(() => {
                    handleClick(i, j, true);
                }, 500);
            });
            
            cell.addEventListener('touchend', () => {
                if (touchTimeout) {
                    clearTimeout(touchTimeout);
                }
            });
            
            updateCellDisplay(cell, gameState.board[i][j]);
            row.appendChild(cell);
        }
        boardElement.appendChild(row);
    }
}

// セルの表示更新
function updateCellDisplay(cell, cellData) {
    cell.className = 'cell'; // ベースクラスをリセット
    
    if (cellData.revealed) {
        cell.classList.add('revealed');
        if (cellData.mine) {
            cell.innerHTML = '💣';
            cell.classList.add('mine');
        } else if (cellData.adjacentMines > 0) {
            cell.innerHTML = cellData.adjacentMines;
            cell.classList.add(`number-${cellData.adjacentMines}`);
        } else {
            cell.innerHTML = '';
        }
    } else if (cellData.flagged) {
        cell.innerHTML = '🚩';
        cell.classList.add('flagged');
    } else {
        cell.innerHTML = '';
    }
}

// クリック処理
async function handleClick(row, col, isRightClick) {
    if (gameState.gameOver) return;

    try {
        const response = await fetch('/api/click', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                row: row,
                col: col,
                rightClick: isRightClick
            })
        });

        const newState = await response.json();
        
        // 状態の更新
        gameState = newState;
        createBoard();
        updateMineCount();

        if (gameState.gameOver) {
            endGame();
        }
    } catch (error) {
        console.error('Error handling click:', error);
    }
}

// 残り地雷数の更新
function updateMineCount() {
    const mineCountElement = document.getElementById('mine-count');
    if (mineCountElement) {
        mineCountElement.textContent = `残り地雷: ${gameState.remainingMines}`;
    }
}

// タイマー更新
function updateTimer() {
    if (!gameState.gameOver) {
        const seconds = Math.floor((Date.now() - gameState.timeStart) / 1000);
        const timerElement = document.getElementById('timer');
        if (timerElement) {
            timerElement.textContent = `経過時間: ${seconds}秒`;
        }
    }
}

function startTimer() {
    if (timerInterval) {
        clearInterval(timerInterval);
    }
    gameState.timeStart = Date.now();
    timerInterval = setInterval(updateTimer, 1000);
}

function endGame() {
    clearInterval(timerInterval);
    const messageElement = document.getElementById('game-message');
    if (messageElement) {
        messageElement.textContent = gameState.victory ? 
            '🎉 おめでとうございます！勝利です！' : 
            '💥 ゲームオーバー';
    }
}

// 新しいゲームを開始
function startNewGame() {
    initGame();
    const messageElement = document.getElementById('game-message');
    if (messageElement) {
        messageElement.textContent = '';
    }
}

// ページ読み込み時に新しいゲームを開始
window.onload = () => {
    initGame();
    
    // New Game ボタンのイベントリスナー
    const newGameButton = document.getElementById('new-game');
    if (newGameButton) {
        newGameButton.addEventListener('click', startNewGame);
    }
};