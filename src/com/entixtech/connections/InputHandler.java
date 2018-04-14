package com.entixtech.connections;

public interface InputHandler {
    public void startReading();
    public void stopReading();
    public void startSending(String s);
    public void sendMessage(Object o);
    public boolean isAlive();
    public boolean isConnected();
}
