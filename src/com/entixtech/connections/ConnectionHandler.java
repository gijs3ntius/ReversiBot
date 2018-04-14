package com.entixtech.connections;

import com.entixtech.parsers.Response;
import com.entixtech.parsers.ResponseParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ConnectionHandler extends AbstractInputHandler {
    private ExecutorService threadPool;
    private Socket connection;
    private PrintWriter output;
    private BufferedReader input;
    private String serverIP;
    private int port;
    private boolean isConnected;
    private boolean inError; // used if an exception crashed the object to indicate a new connections has to be made
    private boolean isLoggedIn;
    private ResponseParser responseParser;

    // constructor for ConnectionHandler
    public ConnectionHandler(Observer o, String serverIP, int port) {
        threadPool = newFixedThreadPool(4); // number of threads needs to be discussed
        responseParser = new ResponseParser();
        addObserver(o);
        this.serverIP = serverIP;
        this.port = port;
        isLoggedIn = false;
        isConnected = false;
        inError = false;
        connect();
    }

    /**
     * Starts a connections with a specified server and sets the
     * isConnected flag to true
     */
    private void connect() {
        try {
            connection = new Socket(serverIP, port); // connect with server using the IP + port from the instance
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new PrintWriter(connection.getOutputStream(), true);
        } catch (IOException e) {
            inError = true;
        }
        isConnected = true;
    }

    /**
     * Disconnects from the server and sets the
     * isConnected flag to false
     */
    public void disconnect() throws IOException
    {
        isConnected = false;
        sendCommandToServer("disconnect"); // disconnect from server
        try {
            connection.close(); // close socket
        } catch (IOException e) {
            inError = true;
        }
    }

    public void login(String username) {
        isLoggedIn = true;
        sendCommandToServer("login " + username);
    }

    public void getGameList() {
        sendCommandToServer("get gamelist");
    }

    public void getPlayerList() {
        sendCommandToServer("get playerlist");
    }

    public void subscribe(String gametype) {
        sendCommandToServer("subscribe " + gametype);
    }

    /**
     *
     * @param move is nog onduidelijk
     */
    public void move(String move) {
        sendCommandToServer("move " + move);
    }

    public void forfeit() {
        sendCommandToServer("forfeit");
    }

    public void challenge(String player, String gametype) {
        sendCommandToServer("challenge " + player + " " + gametype); // misschien moeten er nog "" om game type en player
    }

    public void acceptChallenge(int challengeNumber) {
        sendCommandToServer("challenge accept " + challengeNumber);
    }


    /**
     * Receives a command and sends this to the specified server.
     * Uses a thread to send the command to the server this way the client acts more responsive.
     * @param command command send to server
     */
    private void sendCommandToServer(String command)
    {
        threadPool.submit(() -> {
            output.println(command);
        });
    }

    @Override
    public void startReading() {
        threadPool.submit(new ServerResponseListener());
    }

    @Override
    public void stopReading() {
        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startSending(String s) {
        sendCommandToServer(s);
    }

    /**
     * inner class that is used to listen to the server the connections handler is connected to
     * responds to the registered client with update functions according to the type of respond of the server
     */
    private class ServerResponseListener implements Runnable {

        @Override
        public void run() {
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    Response response = responseParser.parseServerResponse(line);
                    System.out.println(response);
                    notifyObservers(response);
                }
            } catch (IOException i) { // IOException can happen due to the readline function
                i.printStackTrace(); // TODO use a logger to log exceptions
            }
        }
    }
}