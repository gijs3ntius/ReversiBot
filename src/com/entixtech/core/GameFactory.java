package com.entixtech.core;

import java.util.Observer;

public class GameFactory {

    public static AbstractGame createAIvsAIGame(Observer o) {
        return new ReversiGame(true, true, o);
    }
    public static AbstractGame createAIvsHumanGame(Observer o) {
        return new ReversiGame(true, false, o);
    }
    public static AbstractGame createHumanvsAiGame(Observer o) {
        return new ReversiGame(false, true, o);
    }
    public static AbstractGame createHumanvsHumanGame(Observer o) {
        return new ReversiGame(false, false, o);
    }
}
