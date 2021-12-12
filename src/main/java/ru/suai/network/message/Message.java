package ru.suai.network.message;

import java.io.Serializable;
import java.util.Set;

/**
 * Message class.
 * 1. Stores the Type of message being transmitted, specified in the "MessageType" list;
 * 2. Message text;
 * 3. List (this can be either a list of boards or a list of users).
 */

public class Message implements Serializable {
    private final MessageType typeMessage;

    private final String textMessage;

    private final Set<String> list;

    public Message(MessageType typeMessage, String textMessage) {
        this.textMessage = textMessage;
        this.typeMessage = typeMessage;
        this.list = null;
    }

    public Message(MessageType typeMessage, Set<String> list) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.list = list;
    }

    public Message(MessageType typeMessage) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.list = null;
    }

    public MessageType getTypeMessage() {
        return typeMessage;
    }

    public Set<String> getList() {
        return list;
    }

    public String getTextMessage() {
        return textMessage;
    }
}
