package com.entixtech.controllers;

import com.entixtech.connections.AbstractInputHandler;
import com.entixtech.connections.ConnectionHandler;
import com.entixtech.core.AbstractGame;
import com.entixtech.core.Game;
import com.entixtech.core.GameFactory;
import com.entixtech.parsers.Response;
import com.entixtech.parsers.ResponseParser;

import java.util.*;

public class GameController extends AbstractController {
    private AbstractInputHandler interfaceHandler, connectionHandler = null;
    private boolean connected = false;
    private ResponseParser responseParser;
    private AbstractGame game;
    private boolean loggedIn;
    private String username = null;
    private boolean tournamentMode = false;

    /**
     * Core class of the game application
     * needs an interface to interact with
     * @param interfaceHandler interface of the application
     */
    public GameController(AbstractInputHandler interfaceHandler) {
//        interfaceHandler = new CommandLineHandler(this); // the interface
        this.interfaceHandler = interfaceHandler;
        this.interfaceHandler.addObserver(this);
        interfaceHandler.startReading();
        responseParser = new ResponseParser();
    }

    /**
     * Only used in development mode TODO add annotation to prevent using in production mode
     * @param ip
     * @param port
     * @param username
     */
    public void autoInitialize(String ip, int port, String username) {
        connect(ip, port);
        if (connected) {
            connectionHandler.startSending("login " + username);
            loggedIn = true;
            this.username = username;
        }
    }

    private void connect(String ip, int port) {
        connectionHandler = new ConnectionHandler(this, ip, port);
        connectionHandler.addObserver(this);
        connectionHandler.startReading();
        connected = connectionHandler.isConnected();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(interfaceHandler)) { // check if the interface notify'ed the controller
            if (arg instanceof String) {
                handleCommand(responseParser.parseStringResponse((String) arg));
            }
        } else if (o instanceof ConnectionHandler) {
            if (arg instanceof Response) {
                handleResponse((Response) arg);
            }
        } else if (o instanceof Game) {
            if (arg instanceof Integer) {
                if (connected) connectionHandler.startSending("move " + (int) arg); // first check if connected
//                else game.switchPlayer(); // if not connected just switch player
            }
            if (arg instanceof Game) {
                interfaceHandler.sendMessage(arg);
            }
            if (arg instanceof String) {
//                String[] response = ((String) arg).split(" ", 2);
                interfaceHandler.sendMessage(arg);
            }
        }
    }

    // TODO fix responses from server
    private void handleResponse(Response response) {
        switch (response.getType()) {
            case NONE: break; // message from the server is not useful for the application
            case ERROR:
                interfaceHandler.sendMessage(response.getMap().get("error"));
                break; // server responds with an error (e.g. invalid move)
            case CONFIRM:
                interfaceHandler.sendMessage("Operation successful");
                break; // confirms a message send to the server
            case WIN: break; // server indicated the player or computer has won
            case LOSS: break; // server indicated the player or computer has lost
            case DRAW: break; // server indicated the game ended in a draw
            case YOUR_TURN:
                break; // server indicated it is our turn
            case PLAYER_LIST:
                interfaceHandler.sendMessage(response.getList());
                break; // server responds with the requested player list
            case MOVE:
                if (!response.getMap().get("PLAYER").equals(username)) {// did the opponent made a move
                    game.setMove(Integer.parseInt(response.getMap().get("MOVE"))); // update the game with the move
                }
                break; // server responds with a move done by us or the opponent
            case MATCH:
                // TODO build the right game object
                if (tournamentMode) {
                    if (response.getMap().get("PLAYERTOMOVE").equals(username)) { // our turn to begin
                        game = GameFactory.createAIvsHumanGame(this); // here the ai starts
                    } else { // if we do not begin with playing
                        game = GameFactory.createHumanvsAiGame(this); // human starts playing
                    }
                } else game = GameFactory.createHumanvsHumanGame(this); // manual play
                break; // server indicates that a match is started and has information about it
            case GAME_LIST:
                interfaceHandler.sendMessage(response.getList());
                break; // server responds with the requested gamelist
            case CHALLENGE_CANCELLED: break; // challenge send by us is rejected
            case CHALLENGE_RECEIVED:
                if (tournamentMode) {
                    connectionHandler.startSending("challenge accept " + response.getMap().get("CHALLENGENUMBER"));
                } else {
                    interfaceHandler.sendMessage(response); // send the response to the interface and let the user decide
                }
                break; // challenge is send to us
        }
    }

    private String getHelpList() {
        return "login 'player name'\t (when connected with a game server)\n" +
                "connect 'ip' 'port'\n" +
                "match 'type' 'human/ai' 'human/ai'\n" +
                "get playerlist\t (when connected with a game server)\n" +
                "get gamelist\n" +
                "challenge 'game type' 'player name'\t (when connected with a game server)\n" +
                "mode tournament\n" +
                "mode manual";
    }

    private void handleCommand(Response command) {
        switch (command.getResponseType()) {
            case EXIT:
                if (connected) connectionHandler.stopReading();
                interfaceHandler.sendMessage("Good bye!");
                System.exit(0);
                break; // shutdown the application
            case TOURNAMENT_MODE:
                tournamentMode = true;
                if (connected && loggedIn) {
                    // TODO send subscribe message to game server
                }
                interfaceHandler.sendMessage("Tournament mode: Accepting all challenges & games played by an AI");
                break;
            case HELP:
                interfaceHandler.sendMessage(getHelpList());
                break; // sends a help message to the user
            case MANUAL_MODE:
                tournamentMode = false;
                break;
            case LOGIN:
                if (connected) {
                    connectionHandler.startSending("login " + command.getCommandValue()[0]);
                    loggedIn = true;
                    username = command.getCommandValue()[0];
                }
                else interfaceHandler.sendMessage("Not connected! try connect 'ip' 'port' first");
                break;
            case CHALLENGE_ACCEPT:
                if (connected && loggedIn) connectionHandler.startSending("challenge accept " + command.getCommandValue()[0]);
                break;
            case CONNECT:
                if (connected) connectionHandler.stopReading();
                else connect(command.getCommandValue()[0], Integer.parseInt(command.getCommandValue()[1]));
                break;
            case PLAYER_LIST:
                if (connected) connectionHandler.startSending("get playerlist");
                else interfaceHandler.sendMessage("Not connected to game server");
                break;
            case GAME_LIST:
                if (connected) connectionHandler.startSending("get gamelist");
                else interfaceHandler.sendMessage("Reversi");
                break;
            case CHALLENGE_SEND:
                if (connected && loggedIn) connectionHandler.startSending("challenge " +
                        '"' + command.getCommandValue()[0] + '"' +
                        '"' + command.getCommandValue()[1] + '"');
                break;
            case MOVE:
                if (connected && loggedIn) connectionHandler.startSending("move " + command.getCommandValue()[0]);
                else game.setMove(Integer.parseInt(command.getCommandValue()[0]));
                break;
            case MATCH:
                if (command.getCommandValue()[0].equals("reversi")) {
                    if (command.getCommandValue()[1].equals("ai")) {
                        if (command.getCommandValue()[2].equals("human")) game = GameFactory.createAIvsHumanGame(this);
                        if (command.getCommandValue()[2].equals("ai")) game = GameFactory.createAIvsAIGame(this);
                    } else if (command.getCommandValue()[1].equals("human")) {
                        if (command.getCommandValue()[2].equals("human")) game = GameFactory.createHumanvsHumanGame(this);
                        if (command.getCommandValue()[2].equals("ai")) game = GameFactory.createHumanvsAiGame(this);
                    }
                }
                break;
            case NONE:
                interfaceHandler.sendMessage("Unknown argument \nTry one of the following:\n" + getHelpList());
        }
    }
}
