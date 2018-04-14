package com.entixtech.connections;

import com.entixtech.core.Game;
import com.entixtech.helpers.ReversiGameHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class CommandLineHandler extends AbstractInputHandler {
    private PrintWriter output;
    private BufferedReader input;
    private ExecutorService threadPool;

    public CommandLineHandler() {
        threadPool = newFixedThreadPool(2);
        input = new BufferedReader(new InputStreamReader(System.in));
        output = new PrintWriter(System.out, true);
    }

    @Override
    public void startReading() {
        threadPool.submit(new CommandLineResponseListener());
    }

    @Override
    public void stopReading() {
        try {
            input.close(); // close socket
        } catch (IOException e) {
            alive = false;
        }
    }

    @Override
    public void startSending(String s) {
        threadPool.submit(() -> output.println(s));
    }

    @Override
    public void sendMessage(Object o) {
        if (o instanceof Game) {
            if (((Game) o).isFinished()) {
                System.out.println("Player who won the game is: " + (((Game) o).whoWon() == ReversiGameHelper.BLACK ? "black" : "white"));
            } else {
                System.out.println(o);
            }
        } else {
            System.out.println(o); // fot the command line simply print everything else then game updates
        }
    }

    private class CommandLineResponseListener implements Runnable {

        @Override
        public void run() {
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    setChanged();
                    notifyObservers(line);
                }
            } catch (Exception e) {
                connected = false;
            }
        }
    }
}
