package com.entixtech.core;

import java.util.Observable;
import java.util.Observer;

public abstract class AbstractGame extends Observable implements Game {
    protected String gameName;
    protected GameType gameType = GameType.LOCAL; // standard game type is local
    protected boolean runnning = true;

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }
}
