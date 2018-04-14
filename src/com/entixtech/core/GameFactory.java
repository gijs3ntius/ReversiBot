package com.entixtech.core;

import java.util.Observer;

public class GameFactory {

    public static AbstractGame createAIvsAIGame() { return new ReversiGame(true, true); }
    public static AbstractGame createAIvsHumanGame() {
        return new ReversiGame(true, false);
    }
    public static AbstractGame createHumanvsAiGame() { return new ReversiGame(false, true); }
    public static AbstractGame createHumanvsHumanGame() {return new ReversiGame(false, false); }

    public static AbstractGame createAIvsAIGame(Observer o) { return new ReversiGame(true, true, o); }
    public static AbstractGame createAIvsHumanGame(Observer o) {
        return new ReversiGame(true, false, o);
    }
    public static AbstractGame createHumanvsAiGame(Observer o) { return new ReversiGame(false, true, o); }
    public static AbstractGame createHumanvsHumanGame(Observer o) {return new ReversiGame(false, false, o); }

    public static AbstractGame createAIvsAIGame(Observer o, GameType g) { return new ReversiGame(true, true, o, g); }
    public static AbstractGame createAIvsHumanGame(Observer o, GameType g) {
        return new ReversiGame(true, false, o, g);
    }
    public static AbstractGame createHumanvsAiGame(Observer o, GameType g) { return new ReversiGame(false, true, o, g); }
    public static AbstractGame createHumanvsHumanGame(Observer o, GameType g) {return new ReversiGame(false, false, o, g); }
}
