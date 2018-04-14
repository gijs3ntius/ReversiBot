package com.entixtech.core;

import com.entixtech.helpers.ReversiGameHelper;
import java.util.Arrays;
import java.util.Observer;

public class ReversiGame extends AbstractGame {
    private int[][] currentBoard;
    private ReversiGameHelper helper;
    private int playingSide;
    private Player player1;
    private Player player2;

    /**
     *
     * @param player1Auto is player 1 a bot?
     * @param player2Auto is player 2 a bot?
     */
    ReversiGame(boolean player1Auto, boolean player2Auto) {
        this.gameName = "Reversi";
        helper = new ReversiGameHelper();
        currentBoard = helper.getNewBoard();
        player1 = new Player(player1Auto, ReversiGameHelper.WHITE);
        player2 = new Player(player2Auto, ReversiGameHelper.BLACK);
        playingSide = ReversiGameHelper.WHITE;
    }

    ReversiGame(boolean player1Auto, boolean player2Auto, Observer o) {
        this(player1Auto, player2Auto);
        addObserver(o);
        setChanged();
        notifyObservers("Game successfully started");
        setChanged();
        notifyObservers(this);
        setChanged();
        if (!player1Auto) {
            notifyObservers("It is your turn! choose one of the following moves: "
                    + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
        } else {
            playMoveAuto(); // if it is a bot play a move to start the match
        }
    }

    ReversiGame(boolean player1Auto, boolean player2Auto, Observer o, GameType gameType) {
        this(player1Auto, player2Auto, o);
        setGameType(gameType);
    }

    @Override
    public int[][] getBoard() {
        return currentBoard;
    }

    @Override
    public void setMove(int move) {
        if (helper.isValidMove(currentBoard, playingSide, move)) {
            currentBoard = helper.getUpdatedBoard(currentBoard, move, playingSide);
            if (!isFinished()) {
                switchPlayer();
                if (getCurrentPlayer().isAuto()) {
                    playMoveAuto();
                }
            } else {
                setChanged();
                notifyObservers(this);
            }
        } else {
            setChanged();
            notifyObservers("This is not a valid move please try again one of the following: "
                    + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
        }
    }

    private void playMoveAuto() {
        int nextMove = helper.getNextMove(currentBoard, playingSide);
        if (nextMove != -1) {
            currentBoard = helper.getUpdatedBoard(currentBoard, nextMove, playingSide);
            setChanged();
            notifyObservers(nextMove);
            setChanged();
            notifyObservers(this); // check if setChanged() should be called once, twice or not at all
            switchPlayer();
        } else {
            switchPlayer();
        }
    }

    @Override
    public void switchPlayer() {
        playingSide = helper.getOppositeColour(playingSide);
        if (helper.getValidMoves(playingSide, currentBoard).length < 0) {
            playingSide = helper.getOppositeColour(playingSide);
        }
        if (getCurrentPlayer().isAuto()) { // TODO check if this fixes the problem with playing without server
            playMoveAuto();
        } else {
            setChanged();
            notifyObservers("It is your turn! choose one of the following moves: "
                    + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
        }
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (int[] aBoard : currentBoard) {
            for (int anABoard : aBoard) {
                if (anABoard == 0) {
                    result.append("○ ");
                } else if (anABoard == 1) {
                    result.append("● ");
                } else {
                    result.append("X ");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    @Override
    public String getName() {
        return gameName;
    }

    @Override
    public boolean isFinished() {
        if (helper.findCheckedPossibleMoves(playingSide, currentBoard).size() > 1) return false;
        if (helper.findCheckedPossibleMoves(helper.getOppositeColour(playingSide), currentBoard).size() > 1) return false;
        return true; // no moves left for both sides
    }

    @Override
    public int whoWon() {
        return helper.whoWon(currentBoard);
    }

    @Override
    public int[][] getPlayBoard() {
        return currentBoard;
    }

    private Player getCurrentPlayer() {
        return playingSide == ReversiGameHelper.WHITE ? player1 : player2;
    }

    private class Player {
        private final boolean auto;
        private final int colour;

        Player(boolean autonomous, int colour) {
            auto = autonomous;
            this.colour = colour;
        }

        public boolean isAuto() {
            return auto;
        }

        public int getColour() {
            return colour;
        }
    }
}
