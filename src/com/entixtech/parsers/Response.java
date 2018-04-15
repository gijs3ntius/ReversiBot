package com.entixtech.parsers;

import java.util.List;
import java.util.Map;

/**
 * Class used to build representable responds from an input channel
 * Input channel sends text and this class represents that text data in a workable object
 */
public class Response {
    private MessageType messageType;
    private ResponseType responseType;
    private  String[] commandValue;

    private Map<String, String> map;
    private List<String> list;

    Response(ResponseType type) {
        messageType = MessageType.RESPONSE;
        responseType = type;
        map = null;
        list = null;
    }

    public Response(ResponseType type, String[] value) {
        messageType = MessageType.COMMAND;
        responseType = type;
        commandValue = value;
    }

    void setMap(Map<String, String> map) {
        this.map = map;
    }

    void setList(List<String> list) {
        this.list = list;
    }

    public ResponseType getType() {
        return responseType;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public List<String> getList() {
        return list;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public String[] getCommandValue() {
        if (messageType.equals(MessageType.COMMAND)) {
            return commandValue;
        }
        else throw new NullPointerException(); // not possible to ask for command value when response type is not a command
    }

    @Override
    public String toString() {
        if (map != null) {
            return responseType.name() + " " + map.toString();
        } else if (list != null) {
            return responseType.name() + " " + list.toString();
        } else {
            return responseType.name();
        }
    }
}
