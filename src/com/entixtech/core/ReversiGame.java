package com.entixtech.core;

import com.entixtech.helpers.GameHelper;
import com.entixtech.helpers.ReversiGameHelper;
import com.entixtech.parsers.Response;
import com.entixtech.parsers.ResponseType;

import java.util.Arrays;
import java.util.Observer;

//TODO fix the two errors regarding games finished and server connection with local games (see screen shots)
public class ReversiGame extends AbstractGame {
    private int[][] currentBoard;
    private GameHelper helper;
    private int playingSide;
    private boolean started; // used when in a serverGame
    private Player player1;
    private Player player2;

    /**
     *
     * @param player1Auto is player 1 a bot?
     * @param player2Auto is player 2 a bot?
     */
    ReversiGame(boolean player1Auto, boolean player2Auto) {
        this.gameName = "Reversi";
        helper = new ReversiGameHelper(); // this could be any game helper corresponding to the game played
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
        if (!player1Auto) {
            if (gameType == GameType.LOCAL) {
                setChanged();
                notifyObservers("It is your turn! choose one of the following moves: "
                        + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
            }
        } else {
            playMoveAuto(); // if it is a bot play a move to start the match
        }
    }

    ReversiGame(boolean player1Auto, boolean player2Auto, Observer o, GameType gameType) {
        this(player1Auto, player2Auto, o);
        setGameType(gameType);
    }

    ReversiGame(boolean player1Auto, boolean player2Auto, Observer o, boolean started) {
        this(player1Auto, player2Auto, o, GameType.SERVER);
        this.started = started;
    }

    public void yourTurn() {
        if (gameType == GameType.SERVER) {
            if (playingSide == GameHelper.BLACK) {
                if (!player2.isAuto() && !started) {
                    setChanged();
                    notifyObservers("It is your turn! choose one of the following moves: "
                            + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
                }
            } else {
                if (!player1.isAuto() && started) {
                    setChanged();
                    notifyObservers("It is your turn! choose one of the following moves: "
                            + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
                }
            }
        } else {
            setChanged();
            notifyObservers("It is your turn! choose one of the following moves: "
                    + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
        }
    }

    @Override
    public int[][] getBoard() {
        return currentBoard;
    }

    @Override
    public void setMove(int move) {
        if (runnning) {
            if (helper.isValidMove(currentBoard, playingSide, move)) {
                currentBoard = helper.getUpdatedBoard(currentBoard, move, playingSide);
                setChanged();
                notifyObservers(this);
                if (!isFinished()) {
                    switchPlayer();
                    if (getCurrentPlayer().isAuto()) {
                        playMoveAuto();
                    }
                } else {
                    setChanged();
                    String[] won = whoWon() == GameHelper.WHITE ? new String[]{"White"} : new String[]{"Black"};
                    notifyObservers(new Response(ResponseType.FINISHED, won));
                }
            } else {
                setChanged();
                notifyObservers("This is not a valid move please try again one of the following: "
                        + Arrays.toString(helper.getValidMoves(playingSide, currentBoard)));
            }
        }
    }

    private void playMoveAuto() {
        if (runnning) {
            if (!isFinished()) {
                int nextMove = helper.getNextMove(currentBoard, playingSide); // returns -1 if it cannot find another move
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
            } else {
                setChanged();
                String[] won = whoWon() == GameHelper.WHITE ? new String[]{"White"} : new String[]{"Black"};
                notifyObservers(new Response(ResponseType.FINISHED, won));
            }
        }
    }

    @Override
    public void switchPlayer() {
        if (runnning) {
            playingSide = helper.getOppositeColour(playingSide);
            if (helper.getValidMoves(playingSide, currentBoard).length < 0) {
                playingSide = helper.getOppositeColour(playingSide);
            }
            if (getCurrentPlayer().isAuto()) { // TODO check if this fixes the problem with playing without server
                playMoveAuto();
            } else {
                yourTurn();
            }
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
    public String getGameName() {
        return gameName;
    }

    @Override
    public boolean isFinished() {
        if (helper.findCheckedPossibleMoves(playingSide, currentBoard).size() > 0) return false;
        if (helper.findCheckedPossibleMoves(helper.getOppositeColour(playingSide), currentBoard).size() > 0) return false;
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

    @Override
    public void stopGame() {
        runnning =false;
        setChanged();
        if (runnning) {
            notifyObservers("Game stopped");
        } else {
            notifyObservers("Game was already stopped");
        }
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
