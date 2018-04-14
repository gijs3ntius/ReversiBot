package com.entixtech.connections;

import java.util.Observable;

public interface InputHandler {
    public void startReading();
    public void stopReading();
    public void startSending(String s);
}
