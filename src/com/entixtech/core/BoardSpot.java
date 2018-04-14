package com.entixtech.core;

public class BoardSpot {
    public final int row;
    public final int col;

    public BoardSpot(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BoardSpot) {
            return row == ((BoardSpot) obj).row && col == ((BoardSpot) obj).col;
        } else {
            return false;
        }
    }
}
