package com.entixtech.core;

/**
 * TODO game moet observers notify'en van moves die hij doet en of hij gewonnen heeft
 */
public class ReversiGame extends AbstractGame {
    private int[][] currentBoard;
    private ReversiGameHelper helper;
    private int playingSide;
    private boolean autonomous;
    private int colour;

    /**
     *
     * @param autonomous defines if the object should decide moves for itself
     */
    public ReversiGame(boolean autonomous) {
        this.gameName = "Reversi";
        helper = new ReversiGameHelper();
        currentBoard = helper.getNewBoard();
        this.autonomous = autonomous;
    }

    @Override
    public void setColour(int colour) {
        this.colour = colour;
    }

    @Override
    public int[][] getBoard() {
        return currentBoard;
    }

    @Override
    public void setMove(int move) {
        currentBoard = helper.getUpdatedBoard(currentBoard, move, playingSide);
        if (autonomous) {
            int nextMove = helper.getNextMoveRecursive(currentBoard, playingSide);
            if (nextMove != -1) {
                currentBoard = helper.getUpdatedBoard(currentBoard, nextMove, playingSide);
                switchPlayer();
                setChanged();
                notifyObservers(nextMove);
                notifyObservers(currentBoard); // check if setChanged() should be called once, twice or not at all
            } else {
                switchPlayer();
            }
        } else { // just notify observers with changed board
            switchPlayer();
            setChanged();
            notifyObservers(currentBoard);
        }
    }

    @Override
    public void switchPlayer() {
        playingSide = helper.getOppositeColour(playingSide);
    }

    @Override
    public String getName() {
        return gameName;
    }

    @Override
    public boolean isFinished() {
        return false; // TODO implement isFinished
    }
}
