package com.entixtech.parsers;

/**
 * Small enum with all the possible response types
 */
public enum ResponseType {
    NONE, CONFIRM, ERROR, YOUR_TURN, MOVE, MATCH, CHALLENGE_RECEIVED, CHALLENGE_CANCELLED, WIN, LOSS,
    DRAW, PLAYER_LIST, GAME_LIST, LOGIN, CONNECT, CHALLENGE_SEND, NO_POSSIBLE_MOVE
}
