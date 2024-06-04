package com.example.ft_hangouts.ui.messages;

import android.telephony.SmsMessage;

import com.example.ft_hangouts.ui.contacts.Contact;

import java.util.ArrayList;

public class Conversation {
    private final Contact contact;
    private ArrayList<SmsMessage> messages;

    public Conversation(Contact contact, ArrayList<SmsMessage> messages) {
        this.contact = contact;
        this.messages = messages;
    }

    public Contact getContact() {
        return contact;
    }

    public ArrayList<SmsMessage> getMessages() {
        return messages;
    }

}
