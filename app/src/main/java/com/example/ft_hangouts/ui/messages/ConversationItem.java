package com.example.ft_hangouts.ui.messages;

import com.example.ft_hangouts.ui.contacts.Contact;

public class ConversationItem {
    private final String LastMessage;
    private final Contact contact;

    public ConversationItem(Contact contact, String LastMessage) {
        this.contact = contact;
        this.LastMessage = LastMessage;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public Contact getContact() {
        return contact;
    }

}
