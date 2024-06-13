package com.example.ft_hangouts.ui.messages;

import android.database.Cursor;

import java.util.Date;

public class Message {
    long id;
    String message;
    long dateReceive;
    long dateSend;
    int status;
    int errorCode;
    boolean isMe;
    long contactId;

    public Message() {
        this.id = -1;
        this.message = "";
        this.dateReceive = 0;
        this.dateSend = 0;
        this.status = 0;
        this.errorCode = 0;
        this.isMe = false;
        this.contactId = -1;
    }

    public Message(String message, long dateReceive, long dateSend, /*int status, int errorCode,*/ boolean isMe, long contactId) {
        this.id = -1;
        this.message = message;
        this.dateReceive = dateReceive;
        this.dateSend = dateSend;
//        this.status = status;
//        this.errorCode = errorCode;
        this.isMe = isMe;
        this.contactId = contactId;
    }

    public Message(Cursor cursor) {
        this.id = cursor.getLong(0);
        this.message = cursor.getString(1);
        this.dateReceive = cursor.getLong(2);
        this.dateSend = cursor.getLong(3);
        this.status = cursor.getInt(4);
        this.errorCode = cursor.getInt(5);
        this.isMe = cursor.getInt(6) == 1;
        this.contactId = cursor.getLong(7);
    }


    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDateReceive() {
        return new Date(dateReceive);
    }

    public Date getDateSend() {
        return new Date(dateSend);
    }

    public long getDateReceiveUnix() {
        return dateReceive;
    }

    public long getDateSendUnix() {
        return dateSend;
    }

    public int getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
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
