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

    public static AbstractGame createServervsAIGame(Observer o) {
        return new ReversiGame(false, true, o, false);
    }
    public static AbstractGame createServervsHumanGame(Observer o) {
        return new ReversiGame(false, false, o, false);
    }
    public static AbstractGame createHumanvsServerGame(Observer o) {
        return new ReversiGame(false, false, o, true);
    }
    public static AbstractGame createAIvsServerGame(Observer o) {
        return new ReversiGame(true, false, o, true);
    }
}
