package com.entixtech.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.abs;

public class ReversiGameHelper {

    public static final int WHITE = 0; // TODO change player computer to black white
    public static final int BLACK = 1;
    public  static final int EMPTY = 2;

    private int[][] weightedBoardA = {
            {100, -1, 5, 2, 2, 5, -1, 100},
            {-1, -10,1, 1, 1, 1,-10, -1},
            {5 , 1,  1, 1, 1, 1,  1,  5},
            {2 , 1,  1, 0, 0, 1,  1,  2},
            {2 , 1,  1, 0, 0, 1,  1,  2},
            {5 , 1,  1, 1, 1, 1,  1,  5},
            {-1,-10, 1, 1, 1, 1,-10, -1},
            {100, -1, 5, 2, 2, 5, -1, 100}
    };

    private int[][] weightedBoardB = {
            {100, -25, 10, 5, 5, 10, -25, 100},
            {-25, -25, 2, 2, 2, 2, -25, -25},
            {10 , 2,  5, 1, 1, 5,  2,  10},
            {5 , 2,  1, 0, 0, 1,  2,  5},
            {5 , 2,  1, 0, 0, 1,  2,  5},
            {10 , 2,  5, 1, 1, 5,  2,  10},
            {-25, -25, 2, 2, 2, 2, -25, -25},
            {100, -25, 10, 5, 5, 10, -25, 100}
    };

    // source for the heuristics:
    // http://www.ai.rug.nl/~mwiering/GROUP/ARTICLES/paper-othello.pdf
    private int[][] weightedBoardC = {
            {80, -26, 24, -1, -5, 28, -18, 76},
            {-23, -39, -18, -9, -6, -8, -39, -1},
            {46, -16, 4, 1, -3, 6, -20, 52},
            {-13, -5, 2, -1, 4, 3, -12, -2},
            {-5, -6, 1, -2, -3, 0, -9, -5},
            {48, -13, 12, 5, 0, 5, -24, 41},
            {-27, -53, -11, -1, -11, -16, -58, -15},
            {87, -25, 27, -1, 5, 36, -3, 100}
    };

    private int[][] getWeightedBoard(int[][] currentBoard) {
        // TODO add mobility heuristic to bot
        return cornerWeightUpdate(currentBoard);
    }

    /**
     * function that turns negative corner values in positive ones when the corner is taken
     * @param currentBoard the current state of the play board
     * @return a weighted board with adjusted values according to the game state
     */
    private int[][] cornerWeightUpdate(int[][] currentBoard) {
        int[][] newWeightedBoard = copyCurrentBoard(weightedBoardA);
        if (currentBoard[0][0] != EMPTY) {
            newWeightedBoard[0][1] = abs(newWeightedBoard[0][1]);
            newWeightedBoard[1][0] = abs(newWeightedBoard[1][0]);
            newWeightedBoard[1][1] = abs(newWeightedBoard[1][1]);
        }
        if (currentBoard[0][7] != EMPTY) {
            newWeightedBoard[0][6] = abs(newWeightedBoard[0][6]);
            newWeightedBoard[1][6] = abs(newWeightedBoard[1][6]);
            newWeightedBoard[1][7] = abs(newWeightedBoard[1][7]);
        }
        if (currentBoard[7][0] != EMPTY) {
            newWeightedBoard[6][0] = abs(newWeightedBoard[6][0]);
            newWeightedBoard[6][1] = abs(newWeightedBoard[6][1]);
            newWeightedBoard[7][1] = abs(newWeightedBoard[7][1]);
        }
        if (currentBoard[7][7] != EMPTY) {
            newWeightedBoard[7][6] = abs(newWeightedBoard[7][6]);
            newWeightedBoard[6][6] = abs(newWeightedBoard[6][6]);
            newWeightedBoard[6][7] = abs(newWeightedBoard[6][7]);
        }
        return newWeightedBoard;
    }

    private int[][] copyCurrentBoard(int[][] board) {
        int[][] newBoard = new int[8][8];
        for (int row=0;row<8;row++) {
            System.arraycopy(board[row], 0, newBoard[row], 0, 8);
        }
        return newBoard;
    }

    /**
     * Function that will return the best move possible in a given board scenario
     * @return integer representing the board location to choose next
     * if no location is left return -1
     */
    public int getNextMove(int[][] board, int colour) {
//        BoardSpot bestMove = findBestMove(board, WHITE);
        BoardSpot bestMove = findBestMove(board, colour);
        if (bestMove != null)
            return bestMove.row*8 + bestMove.col;
        else return -1; // no possible move to make
    }

    /**
     * Function that will return the best move possible in a given board scenario
     * @return integer representing the board location to choose next
     * if no location is left return -1
     */
    public int getNextMoveRandom(int[][] board, int colour) {
        List<BoardSpot> validMoves = findCheckedPossibleMoves(colour, board);
        if (validMoves.size() > 0) {
            Random random = new Random();
            BoardSpot spot = validMoves.get(random.nextInt(validMoves.size()));
            return spot.row*8 + spot.col;
        }
        else return -1; // no possible move to make
    }

    /**
     * Function that will return the best move possible in a given board scenario
     * @return integer representing the board location to choose next
     * if no location is left return -1
     */
    public int getNextMoveRecursive(int[][] board, int colour) {
//        BoardSpot bestMove = findBestMove(board, colour);
        long startTime = System.currentTimeMillis();
        BoardSpot bestMove = findBestMove(board, colour, 10, startTime); // > recursive steps cause slower execution!
        long endTime = System.currentTimeMillis();
        System.out.println((endTime-startTime)/1000);
        if (bestMove != null)
            return bestMove.row*8 + bestMove.col;
        else return -1; // no possible move to make
    }

    /**
     *
     * @param colour is player or computer
     * @return all possible moves for a colour and a board state
     */
    public List<BoardSpot> findCheckedPossibleMoves(int colour, int[][] board) {
        List<BoardSpot> checkedMoves = new LinkedList<>();
        for (int row=0;row<8;row++) {
            for (int col=0;col<8;col++) {
                if (board[row][col] == EMPTY) {
                    BoardSpot tempSpot = new BoardSpot(row, col);
                    if (checkBoardSpot(board, tempSpot, colour)) {
                        checkedMoves.add(tempSpot);
                    }
                }
            }
        }
        return checkedMoves;
    }

    private boolean checkBoardSpot(int[][] board, BoardSpot spot, int colour) {
        return checkHorizontal(board, spot, colour) ||
                checkVertical(board, spot, colour) ||
                checkDiagonalPositive(board, spot, colour) ||
                checkDiagonalNegative(board, spot, colour);
    }

    /**
     * Checks diagonal if a move is valid
     * @param board play board
     * @param spot spot to check from
     * @param colour colour to check (e.g. black, white)
     * @return false if move is false else true
     */
    private boolean checkDiagonalNegative(int[][] board, BoardSpot spot, int colour) {
        int steps = 0, row = spot.row, col = spot.col;
        while (row < 7 && col > 0) { // checks diagonal bottom left
            row++;
            col--;
            steps++;
            if (board[row][col] == EMPTY) { // break when a row or a column is empty
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) {// if a position has your own color + no neighbors return true
                return true;
            }
        }
        steps = 0;
        row = spot.row;
        col = spot.col;
        while (row > 0 && col < 7) { // checks diagonal top right
            row --;
            col++;
            steps++;
            if (board[row][col] == EMPTY) { // break when a row or column is empty
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) { // if a position has your own color + no neighbors return true
                return true;
            }
        }
        return false;
    }

    /**
     * Checks diagonal if a move is valid
     * @param board play board
     * @param spot spot to check from
     * @param colour colour to check (e.g. black, white)
     * @return false if move is false else true
     */
    private boolean checkDiagonalPositive(int[][] board, BoardSpot spot, int colour) {
        int steps = 0, row = spot.row, col = spot.col;
        while (row > 0 && col > 0) { // checks diagonal top left
            row--;
            col--;
            steps++;
            if (board[row][col] == EMPTY) { // break when a row or a column is empty
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) { // if a position has your own color + no neighbors return true
                return true;
            }
        }
        steps = 0;
        row = spot.row;
        col = spot.col;
        while (row < 7 && col < 7) { // diagonal bottom right
            row ++;
            col++;
            steps++;
            if (board[row][col] == EMPTY) { // break when a row or column is empty
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) { // if a position has your own color + no neighbors return true
                return true;
            }
        }
        return false;
    }

    /**
     * Checks vertical if a move is valid
     * @param board play board
     * @param spot spot to check from
     * @param colour colour to check (e.g. black, white)
     * @return false if move is false else true
     */
    private boolean checkVertical(int[][] board, BoardSpot spot, int colour) {
        int steps = 0, row = spot.row, col = spot.col;
        while (row > 0) { // checks vertical up
            row--;
            steps++;
            if (board[row][col] == EMPTY) { // break when a row or col is empty
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) { // return true when it finds your own color + no neighbor
                return true;
            }
        }
        steps = 0;
        row = spot.row;
        col = spot.col;
        while (row < 7) { // checks vertical bottom
            row++;
            steps++;
            if (board[row][col] == EMPTY) { // breaks when a row is empty
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) { // return true when it finds your own color + no neighbor
                return true;
            }
        }
        return false;
    }

    /**
     * Checks horizontal if a move is valid
     * @param board play board
     * @param spot spot to check from
     * @param colour colour to check (e.g. black, white)
     * @return false if move is false else true
     */
    private boolean checkHorizontal(int[][] board, BoardSpot spot, int colour) {
        int steps = 0, row = spot.row, col = spot.col;
        while (col > 0) { // checks horizontal left
            col--;
            steps++;
            if (board[row][col] == EMPTY) {
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) {
                return true;
            }
        }
        steps = 0;
        row = spot.row;
        col = spot.col;
        while (col < 7) { // checks horizontal right
            col++;
            steps++;
            if (board[row][col] == EMPTY) {
                break;
            }
            if (board[row][col] == colour && steps <= 1) {
                break;
            }
            if (board[row][col] == colour && steps > 1) {
                return true;
            }
        }
        return false;
    }

    private BoardSpot findBestMove(int[][] board, int colour) {
        List<BoardSpot> moves = findCheckedPossibleMoves(colour, board); // computer colour is always the opponent
        if (moves.size() < 1) {
            return null; // no move possible
        }
        if (moves.size() == 1) {
            return new BoardSpot(moves.get(0).row, moves.get(0).col); // only one possible move
        }
        List<BoardSpot> bestMoves = new LinkedList<>();
        for (BoardSpot spot : moves) {
            if (bestMoves.size() == 0) {
                bestMoves.add(spot);
            } else {
                BoardSpot tempSpot = bestMoves.get(0);
                if (getWeightedBoard(board)[spot.row][spot.col] > getWeightedBoard(board)[tempSpot.row][tempSpot.col]) {
                    bestMoves.clear();
                    bestMoves.add(spot);
                } else if (getWeightedBoard(board)[spot.row][spot.col] == getWeightedBoard(board)[tempSpot.row][tempSpot.col]) {
                    bestMoves.add(spot);
                }
            }
        }
        if (bestMoves.size() > 1) {
            Random random = new Random();
            return bestMoves.get(random.nextInt(bestMoves.size())); // TODO make a better choosing algorithm
        } else {
            return  bestMoves.get(0);
        }
    }

    /**
     * Function that uses a recursive function to find the best move
     * @param board the board state where the move has to be made
     * @param colour the colour of the side to make a move
     * @param recursiveSteps recursive depth to make the program lighter
     * @return BoardSpot representing the best move to be made according to the algorithm
     */
    private BoardSpot findBestMove(int[][] board, int colour, int recursiveSteps, long time) {
        BoardSpot bestMove = null;
        int value = -MAX_VALUE; // makes sure there is at least a move bigger than the value
        for (BoardSpot move : findCheckedPossibleMoves(colour, board)) {
            int[][] updatedBoard = getUpdatedBoard(board, calcMove(move), colour);
            int tempValue = findDirectValue(move, updatedBoard) + findIndirectValue(recursiveSteps, time, colour, updatedBoard);
            System.out.println(tempValue);
            if (tempValue > value) {
                bestMove = move;
                value = tempValue;
            }
        }
        return bestMove != null ? bestMove : findBestMove(board, colour); // tries to find the best spot or uses simple finder
    }

    private int findDirectValue(BoardSpot move, int[][] board) {
        return getWeightedBoard(board)[move.row][move.col];
    }

    private int findIndirectValue(int steps, long time, int colour, int[][] board) {
        if (steps < 1 || System.currentTimeMillis() - time > 9000000) {
            double inDirectValue = 0;
            List<BoardSpot> moves = findCheckedPossibleMoves(getOppositeColour(colour), board);
            for (BoardSpot opponentMove : moves) {
                int[][] updatedBoard = getUpdatedBoard(board, calcMove(opponentMove), getOppositeColour(colour));
                List<BoardSpot> recMoves = findCheckedPossibleMoves(colour, updatedBoard);
                for (BoardSpot recMove : recMoves) {
                    inDirectValue += findDirectValue(recMove, updatedBoard);
                }
                inDirectValue /= recMoves.size();
            }
            inDirectValue -= calculateBoardWeight(getOppositeColour(colour), board);
            inDirectValue /= moves.size();
            return (int) inDirectValue;
        } else {
            double inDirectValue = 0;
            List<BoardSpot> moves = findCheckedPossibleMoves(getOppositeColour(colour), board);
            for (BoardSpot opponentMove : moves) {
                int[][] updatedBoard = getUpdatedBoard(board, calcMove(opponentMove), getOppositeColour(colour));
                List<BoardSpot> recMoves = findCheckedPossibleMoves(colour, updatedBoard);
                for (BoardSpot recMove : recMoves) {
                    inDirectValue += findDirectValue(recMove, updatedBoard) + findIndirectValue(--steps, time, colour, updatedBoard); // recursive call
                }
                inDirectValue /= recMoves.size();
            }
            inDirectValue -= calculateBoardWeight(getOppositeColour(colour), board);
            inDirectValue /= moves.size();
            return (int) inDirectValue;
        }
    }

    private int calcMove(BoardSpot move) {
        return move.row * 8 + move.col;
    }

    public int getOppositeColour(int colour) {
        return colour == WHITE ? BLACK : WHITE;
    }

    public int[][] getUpdatedBoard(int[][] board, int move, int colour) {
        BoardSpot temp = new BoardSpot(move/8, move%8);
        int[][] updatedBoard = copyCurrentBoard(board);
        updateHorizontalSpots(updatedBoard, temp, colour);
        updateVerticalSpots(updatedBoard, temp, colour);
        updateDiagonalPositiveSpots(updatedBoard, temp, colour);
        updateDiagonalNegativeSpots(updatedBoard, temp, colour);
        return updatedBoard;
    }

    private void updateHorizontalSpots(int[][] board, BoardSpot move, int colour) {
        int row = move.row, col = move.col;
        while (row > 0) {
            row--;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.row;i>row;i--) {
                    board[i][col] = colour;
                }
                break;
            }
        }
        // start searching the opposite direction
        row = move.row;
        col = move.col;
        while (row < 7) {
            row++;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.row;i<row;i++) { // start converting
                    board[i][col] = colour; // convert
                }
                break; // stop converting
            }
        }
    }

    private void updateVerticalSpots(int[][] board, BoardSpot move, int colour) {
        int row = move.row, col = move.col;
        while (col > 0) {
            col--;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.col;i>col;i--) {
                    board[row][i] = colour;
                }
                break;
            }
        }
        // start searching the opposite direction
        row = move.row;
        col = move.col;
        while (col < 7) {
            col++;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.col;i<col;i++) { // start converting
                    board[row][i] = colour; // convert
                }
                break; // stop converting
            }
        }
    }

    private void updateDiagonalNegativeSpots(int[][] board, BoardSpot move, int colour) {
        int row = move.row, col = move.col;
        while (col > 0 && row > 0) {
            col--;
            row--;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.col,j=move.row;i>col&&j>row;i--,j--) {
                    board[j][i] = colour;
                }
                break;
            }
        }
        // start searching the opposite direction
        row = move.row;
        col = move.col;
        while (col < 7 && row < 7) {
            col++;
            row++;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.col,j=move.row;i<col&&j<row;i++, j++) {
                    board[j][i] = colour;
                }
                break;
            }
        }
    }

    private void updateDiagonalPositiveSpots(int[][] board, BoardSpot move, int colour) {
        int row = move.row, col = move.col;
        while (col < 7 && row > 0) {
            col++;
            row--;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.col,j=move.row;i<col&&j>row;i++, j--) {
                    board[j][i] = colour;
                }
                break;
            }
        }
        // start searching the opposite direction
        row = move.row;
        col = move.col;
        while (col > 0 && row < 7) {
            col--;
            row++;
            if (board[row][col] == EMPTY) {
                break;
            } else if (board[row][col] == colour) { // if same colour is encountered colour all the tiles in between
                for (int i=move.col,j=move.row;i>col&&j<row;i--, j++) {
                    board[j][i] = colour;
                }
                break;
            }
        }
    }

    public int[][] getNewBoard() {
        return new int[][]{
                {2,2,2,2,2,2,2,2},
                {2,2,2,2,2,2,2,2},
                {2,2,2,2,2,2,2,2},
                {2,2,2,1,0,2,2,2}, // 1 --> 0 & 0 --> 1
                {2,2,2,0,1,2,2,2}, // 1 --> 0 & 0 --> 1
                {2,2,2,2,2,2,2,2},
                {2,2,2,2,2,2,2,2},
                {2,2,2,2,2,2,2,2},
        };
    }

    /**
     * Calculate board weight of possible moves for a colour
     * @param colour colour to check (e.g. black or white)
     * @return calculated board weight for colour
     */
    private int calculateBoardWeight(int colour, int[][] board) {
        double total = 0;
        List<BoardSpot> possibleMoves = findCheckedPossibleMoves(colour, board);
        if (possibleMoves.size() < 1) {
            return 0;
        }
        for (BoardSpot spot: possibleMoves) {
            total += getWeightedBoard(board)[spot.row][spot.col];
        }
        return (int) total/possibleMoves.size();
    }

    public static void printBoard(int[][] board)
    {
        StringBuilder result = new StringBuilder();
        for (int[] aBoard : board) {
            for (int anABoard : aBoard) {
                if (anABoard == 0) {
                    result.append('O');
                } else if (anABoard == 1) {
                    result.append('X');
                } else {
                    result.append("*");
                }
            }
            result.append("\n");
        }
        System.out.println(result.toString());
    }

    /**
     * Function that returns which side won, or a draw
     * @param board the play board
     * @return integer representing the player who won
     */
    public int whoWon(int[][] board) {
        int totalWhite = getScore(board, WHITE);
        int totalBlack = getScore(board, BLACK);
        if (totalBlack == totalWhite) return EMPTY;
        return totalWhite > totalBlack ? WHITE : BLACK;
    }

    /**
     * Function that returns the current score of a colour on a board
     * @param board the play board
     * @param colour the side which score needs to be counted
     * @return an integer representing the score of a colour
     */
    public int getScore(int[][] board, int colour) {
        int totalWhite = 0;
        int totalBlack = 0;
        for (int row=0;row<8;row++) {
            for (int col=0;col<8;col++) {
                if (board[row][col] == WHITE) totalWhite++;
                if (board[row][col] == BLACK) totalBlack++;
            }
        }
        return colour == BLACK ? totalBlack : totalWhite;
    }
}
