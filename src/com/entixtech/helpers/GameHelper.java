package com.entixtech.helpers;

import com.entixtech.core.BoardSpot;

import java.util.List;

public interface GameHelper {
    int WHITE = 0;
    int BLACK = 1;
    int EMPTY = 2;

    static void printBoard(int[][] board)
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

    boolean isValidMove(int[][] board, int colour, int move);

    int[] getValidMoves(int colour, int[][] board);

    int getNextMove(int[][] board, int colour);

    int getNextMoveRandom(int[][] board, int colour);

    int getNextMoveRecursive(int[][] board, int colour);

    List<BoardSpot> findCheckedPossibleMoves(int colour, int[][] board);

    int getOppositeColour(int colour);

    int[][] getUpdatedBoard(int[][] board, int move, int colour);

    int[][] getNewBoard();

    int whoWon(int[][] board);

    int getScore(int[][] board, int colour);
}
