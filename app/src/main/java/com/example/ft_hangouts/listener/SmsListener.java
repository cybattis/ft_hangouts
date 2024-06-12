package com.example.ft_hangouts.listener;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.permission.PermissionHandler;
import com.example.ft_hangouts.ui.contacts.Contact;
import com.example.ft_hangouts.ui.messages.Message;

public class SmsListener extends BroadcastReceiver {
    private static final String TAG = "SmsListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!PermissionHandler.isPermissionGranted(context, Manifest.permission.RECEIVE_SMS)) {
            PermissionHandler.requestReceiveSmsPermission(context);
            if (PermissionHandler.isPermissionGranted(context, Manifest.permission.RECEIVE_SMS))
                Log.d(TAG, "SMS received permission granted");
            else {
                Log.e(TAG, "SMS received permission not granted, cannot read SMS");
                return;
            }
        }

        DatabaseHelper db = new DatabaseHelper(context);

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Intent conversationIntent = new Intent("smsBroadCast");
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                Log.d(TAG, "Message received: " + smsMessage.getMessageBody());

                long contactId = db.getContact(smsMessage.getOriginatingAddress()).getContactId();
                if (contactId == -1) {
                    Log.d(TAG, "Contact not found for phone number: " + smsMessage.getOriginatingAddress() + ", creating new contact");

                    // create new contact
                    Contact contact = new Contact();
                    contact.setPhoneNumber(smsMessage.getOriginatingAddress());
                    contactId = db.addContact(contact);

                    // add message
                    Message message = new Message(smsMessage.getMessageBody(), smsMessage.getTimestampMillis(), false, contactId);
                    if (!db.addMessage(message)) {
                        Log.e(TAG, "Failed to add message to database");
                        return ;
                    }

                    conversationIntent.putExtra("contact_id", contactId);
                    context.sendBroadcast(conversationIntent);
                }
                else {
                    Log.d(TAG, "Contact found for phone number: " + smsMessage.getOriginatingAddress() + ", broadcasting message");

                    Message message = new Message(smsMessage.getMessageBody(), smsMessage.getTimestampMillis(), false, contactId);
                    if (!db.addMessage(message)) {
                        Log.e(TAG, "Failed to add message to database");
                        return;
                    }

                    conversationIntent.putExtra("message", smsMessage.getMessageBody());
                    conversationIntent.putExtra("contact_id", contactId);
                    conversationIntent.putExtra("timestamp", smsMessage.getTimestampMillis());
                    context.sendBroadcast(conversationIntent);
                }
            }
        }
    }
}