package com.example.ft_hangouts.ui.messages;

import java.util.ArrayList;

public class Conversation {
    private ArrayList<Message> messages;

    public Conversation(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

}
