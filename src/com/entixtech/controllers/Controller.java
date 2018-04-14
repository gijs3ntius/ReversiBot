package com.entixtech.controllers;

public interface Controller {
    public void sendResponse(Object command);
    public void receiveResponse(Object command);
}
