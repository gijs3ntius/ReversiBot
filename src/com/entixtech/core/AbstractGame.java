package com.entixtech.core;

import java.util.Observable;
import java.util.Observer;

public abstract class AbstractGame extends Observable implements Game {
    protected String gameName;

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }
}
