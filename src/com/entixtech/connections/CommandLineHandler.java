package com.entixtech.connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class CommandLineHandler extends AbstractInputHandler {
    private PrintWriter output;
    private BufferedReader input;
    private ExecutorService threadPool;

    public CommandLineHandler(Observer observer) {
        threadPool = newFixedThreadPool(2);
        addObserver(observer);
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
            e.printStackTrace();
        }
    }

    @Override
    public void startSending(String s) {
        threadPool.submit(() -> output.println(s));
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
                e.printStackTrace();
            }
        }
    }
}
