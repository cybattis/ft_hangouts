package com.example.ft_hangouts.ui.messages;

import android.database.Cursor;

import java.util.Date;

public class Message {
    long id;
    String message;
    long timestamp;
    boolean isMe;
    long contactId;

    public Message() {
        this.id = -1;
        this.message = "";
        this.timestamp = 0;
        this.isMe = false;
        this.contactId = -1;
    }

    public Message(String message, long timestamp, boolean isMe, long contactId) {
        this.id = -1;
        this.message = message;
        this.timestamp = timestamp;
        this.isMe = isMe;
        this.contactId = contactId;
    }

    public Message(Cursor cursor) {
        this.id = cursor.getLong(0);
        this.message = cursor.getString(1);
        this.timestamp = cursor.getLong(2);
        this.isMe = cursor.getInt(3) == 1;
        this.contactId = cursor.getLong(4);
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return new Date(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isMe() {
        return isMe;
    }

    public long getContactId() {
        return contactId;
    }

    public boolean isValid() {
        return id != -1;
    }
}
