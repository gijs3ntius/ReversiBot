package com.entixtech.parsers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseParser {

    // {(\w|\s|"|:|<|>|,)+} or easier {.+} regex for map.
    // \[.+\] regex for list.
    // ([A-Z]+) regex for commands, called after lists and maps are filtered output.
    /**
     *
     * @param responseLine
     * @return
     * @throws NullPointerException
     */
    public Response parseServerResponse(String responseLine) throws NullPointerException {
//        LinkedList<Response> responses = new LinkedList<>(); //List with response objects
//        for (String responseLine : serverResponse) { // accepts multiple response lines
        /* initiate all objects to be used each iteration (per responseLine)*/
        String map = "", list = "";
        Map<String, String> deserializeMap = null;
        List<String> deserializeList = null;
        /* piece of the function that extracts the data from the strings and parses maps or lists*/
        Pattern mapPattern = Pattern.compile("\\{.+}"), listPattern = Pattern.compile("\\[.+]");
        Matcher mapMatcher = mapPattern.matcher(responseLine), listMatcher = listPattern.matcher(responseLine);
        if (mapMatcher.find()) {
            map = mapMatcher.group(0);
            deserializeMap = DeserializeMap(map); //convert String map to Map map
        }
        if (listMatcher.find()) {
            list = listMatcher.group(0);
            deserializeList = DeserializeList(list); //convert String map to Map map
        }
        String[] responseCodes = responseLine.replace(map, "").replace(list, "").split(" ");
        /* piece of the function that creates all the response objects*/
        for (String response : responseCodes) {
            if (response.equals("OK")) {
                Response r = new Response(ResponseType.CONFIRM); // used to be modified
                return r;
            }
            if (response.equals("ERR")) {
                Response r = new Response(ResponseType.ERROR);
                HashMap<String, String> tempMap = new HashMap<>();
                tempMap.put("error", responseLine.replace("ERR ", ""));
                r.setMap(tempMap);
                return r;
            }
            if (response.equals("CHALLENGE") && responseCodes.length >= 4) { // challenge is cancelled
                Response r = new Response(ResponseType.CHALLENGE_CANCELLED);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("CHALLENGE") && responseCodes.length < 4) { // challenge received
                Response r = new Response(ResponseType.CHALLENGE_RECEIVED);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("WIN")) {
                Response r = new Response(ResponseType.WIN);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("LOSS")) {
                Response r = new Response(ResponseType.LOSS);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("DRAW")) {
                Response r = new Response(ResponseType.DRAW);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("MOVE")) {
                Response r = new Response(ResponseType.MOVE);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("YOURTURN")) {
                Response r = new Response(ResponseType.YOUR_TURN);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("MATCH")) {
                Response r = new Response(ResponseType.MATCH);
                r.setMap(deserializeMap);
                return r;
            }
            if (response.equals("GAMELIST")) {
                Response r = new Response(ResponseType.GAME_LIST);
                r.setList(deserializeList);
                return r;
            }
            if (response.equals("PLAYERLIST")) {
                Response r = new Response(ResponseType.PLAYER_LIST);
                r.setList(deserializeList);
                return r;
            }
        }
        return new Response(ResponseType.NONE);
    }

    public Response parseStringResponse(String rawInput) {
        String[] input = rawInput.split(" ");
        if (input.length > 0 && input[0].equals("exit")) {
            return new Response(ResponseType.EXIT, new String[0]);
        }
        if (input.length > 0 && input[0].equals("help")) {
            return new Response(ResponseType.HELP, new String[0]);
        }
        if (input.length > 2 && input[0].equals("connect")) {
            return new Response(ResponseType.CONNECT, Arrays.copyOfRange(input, 1, input.length));
        }
        if (input.length > 1 && input[0].equals("mode")) {
            if (input[1].equals("tournament")) return new Response(ResponseType.TOURNAMENT_MODE, new String[0]);
            else if (input[1].equals("manual")) return new Response(ResponseType.MANUAL_MODE, new String[0]);
        }
        if (input.length > 1 && input[0].equals("login")) {
            return new Response(ResponseType.LOGIN, Arrays.copyOfRange(input, 1, input.length));
        }
        if (input.length > 1 && input[0].equals("move")) {
            return new Response(ResponseType.MOVE, Arrays.copyOfRange(input, 1, input.length));
        }
        if (input.length > 2 && input[0].equals("challenge")) {
            if (input[1].equals("accept")) return new Response(ResponseType.CHALLENGE_ACCEPT, Arrays.copyOfRange(input, 2, input.length));
            else return new Response(ResponseType.CHALLENGE_SEND, Arrays.copyOfRange(input, 1, input.length));
        }
        if (input.length > 1 && input[0].equals("get")) {
            if (input[1].equals("gamelist")) return new Response(ResponseType.GAME_LIST, Arrays.copyOfRange(input, 1, input.length));
            else if (input[1].equals("playerlist")) return new Response(ResponseType.PLAYER_LIST, Arrays.copyOfRange(input, 1, input.length));
        }
        if (input.length > 3 && input[0].equals("match")) return new Response(ResponseType.MATCH, Arrays.copyOfRange(input, 1, input.length));
        return new Response(ResponseType.NONE);
    }

    /**
     * function that converts a string representing a list to a java list object
     * @param list
     * @return
     */
    private List<String> DeserializeList(String list) {
        List<String> result = new LinkedList<>();
        list = list.replace("[", "").replace("]", "").replace("\"", "").replace(" ", "");
        String[] items = list.split(",");
        Collections.addAll(result, items);
        return result;
    }

    /**
     * function that converts a string representing a map to a java map object
     * this function is tested and finished
     * @param map
     * @return
     */
    private Map<String, String> DeserializeMap(String map) {
        Map<String, String> result = new HashMap<String, String>();
        map = map.replace("{", "").replace("}", "").replace("\"", "");
        String[] pairs = map.split(",");
        for (String pair : pairs) {
            pair = pair.replace(" ", "");
            String[] keyValue = pair.split(":");
            if (keyValue.length < 2) {
                String temp = keyValue[0];
                keyValue = new String[] {temp, ""};
            }
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }
}
