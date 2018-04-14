package com.entixtech.controllers;

import com.entixtech.connections.CommandLineHandler;
import com.entixtech.connections.ConnectionHandler;
import com.entixtech.connections.InputHandler;
import com.entixtech.core.Game;
import com.entixtech.parsers.MessageType;
import com.entixtech.parsers.Response;
import com.entixtech.parsers.ResponseParser;

import java.util.*;

public class GameController extends AbstractController {
    private InputHandler interfaceHandler, connectionHandler;
    private boolean connected = false;
    private ResponseParser responseParser;
    private Game game; // TODO Local games are composed of multiple games how to fix this?

    public GameController() {
        interfaceHandler = new CommandLineHandler(this);
        interfaceHandler.startReading();
        responseParser = new ResponseParser();
    }

    public void connect(String ip, int port) {
        connectionHandler = new ConnectionHandler(this, ip, port);
        connected = true;
    }

    public void move() {

    }

    public void displayPlayerList(List<String> list) {

    }

    public void receiveMove(Map<String, String> map) {

    }

    public void displayGameList() {

    }

    public void acceptChallengeRequest(Map<String, String> map) {

    }

    public void receiveMatchData(Map<String, String> map) {

    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof CommandLineHandler) {
            if (arg instanceof String) {
                handleResponse(responseParser.parseCommandLineResponse((String) arg));
            }
        } else if (o instanceof ConnectionHandler) {
            if (arg instanceof Response) {
                receiveResponse(arg);
            }
        } else if (o instanceof Game) {
            // do stuff with the board or move that is sent
        }
    }

    @Override
    public void sendResponse(Object command) {

    }

    @Override
    public void receiveResponse(Object command) {
        if (command instanceof Response) {
            if (((Response) command).getMessageType() == MessageType.COMMAND) receiveResponse(command);
            else {

            }
        }
    }

    private void handleResponse(Response command) {
        switch (command.getResponseType()) {
            case LOGIN:
                if (connected) connectionHandler.startSending("login " + command.getCommandValue()[0]);
                break;
            case CONNECT:
                if (connected) connectionHandler.stopReading();
                else connect(command.getCommandValue()[0], Integer.parseInt(command.getCommandValue()[1]));
                break;
            case PLAYER_LIST:
                if (connected) connectionHandler.startSending("get playerlist");
                break;
            case GAME_LIST:
                if (connected) connectionHandler.startSending("get gamelist");
                break;
            case CHALLENGE_SEND:
                if (connected) connectionHandler.startSending("challenge " + command.getCommandValue()[0] + command.getCommandValue()[1]);
        }
    }
}
