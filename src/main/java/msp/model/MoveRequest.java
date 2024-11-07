package msp.model;

public class MoveRequest {
	private int row;
	private int col;
	private boolean rightClick;

	// ゲッターを追加
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isRightClick() {
		return rightClick;
	}

	// 必要であればセッターも追加
	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setRightClick(boolean rightClick) {
		this.rightClick = rightClick;
	}
}
