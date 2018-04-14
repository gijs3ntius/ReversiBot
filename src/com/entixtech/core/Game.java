package com.entixtech.core;

public interface Game {
    public void setColour(int colour);
    public int[][] getBoard();
    public void setMove(int move);
    public void switchPlayer();
    public String getName();
    public boolean isFinished();
}
