package msp.model;


public class Cell {
	private boolean mine;
	private boolean revealed;
	private boolean flagged;
	private int adjacentMines;
	private String state;
	
	public Cell() {
		this.mine = false;
		this.revealed = false;
		this.flagged = false;
		this.adjacentMines = 0;
		this.state = UNPRESSED; //初期状態は未押下
	}
	
	//Getters and setters 
	public boolean isMine() { return mine;}
	public void setMine(boolean mine) {this.mine = mine;}
	public boolean isRevealed() {return revealed;}
	public void setRevealed(boolean revealed) {this.revealed = revealed;}
	public boolean isFlagged() {return flagged;}
	public void setFlagged(boolean flagged) {this.flagged = flagged;}
	public int getAdjacentMines() {return adjacentMines;}
	public void setAdjacentMines(int adjacentMines) {this.adjacentMines = adjacentMines;}
	
	public String getState() {return state;}
	public void setState(String state) {this.state = state;}
	
	public static final String PRESSED = "PRESSED";
	public static final String CHAIN_REACTION = "CHAIN_REACTION";
	public static final String UNPRESSED = "UNPRESSED";
}
