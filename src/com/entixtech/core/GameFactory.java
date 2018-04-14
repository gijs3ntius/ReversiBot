package com.entixtech.core;

public class GameFactory {

    public static Game createAIGame() {
        return new ReversiGame(true);
    }

    public static Game createHumanGame() {
        return new ReversiGame(false);
    }
}
