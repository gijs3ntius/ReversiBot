package com.entixtech.connections;

import java.util.Observable;
import java.util.Observer;

public abstract class AbstractInputHandler extends Observable implements InputHandler {
    protected boolean alive;
    protected boolean connected;

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
