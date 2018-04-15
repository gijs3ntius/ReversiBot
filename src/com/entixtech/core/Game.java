package com.entixtech.core;

public interface Game {
    public int[][] getBoard();
    public void setMove(int move);
    public void switchPlayer();
    public String getGameName();
    public boolean isFinished();
    public int whoWon();
    public int[][] getPlayBoard();
    public void stopGame();
}
