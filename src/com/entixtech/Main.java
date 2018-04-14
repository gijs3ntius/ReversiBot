package com.entixtech;

import com.entixtech.connections.AbstractInputHandler;
import com.entixtech.connections.CommandLineHandler;
import com.entixtech.controllers.GameController;

public class Main {

    private Main() {
        AbstractInputHandler inputHandler = new CommandLineHandler();
        GameController gameController = new GameController(inputHandler);
        gameController.autoInitialize("localhost", 7789, "Gijs");
    }

    public static void main(String[] args) {
        new Main();
    }
}
