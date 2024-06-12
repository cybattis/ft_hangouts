package com.example.ft_hangouts;

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
import com.example.ft_hangouts.ui.contacts.Contact;
import com.example.ft_hangouts.ui.messages.Message;

public class SmsListener extends BroadcastReceiver {
    private static final String TAG = "SmsListener";
    private Context context;

    public SmsListener(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isSmsReadPermissionGranted()) {
            Log.w(TAG, "SMS read permission not granted");
            return;
        }

        DatabaseHelper db = new DatabaseHelper(context);

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
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
                    Message message = new Message(smsMessage.getMessageBody(), smsMessage.getTimestampMillis(), 0, false, contactId);
                    db.addMessage(message);
                }
                else {
                    Log.d(TAG, "Contact found for phone number: " + smsMessage.getOriginatingAddress() + ", broadcasting message");

                    Intent conversationIntent = new Intent("smsBroadCast");
                    conversationIntent.putExtra("message", smsMessage.getMessageBody());
                    conversationIntent.putExtra("contact_id", contactId);
                    conversationIntent.putExtra("timestamp", smsMessage.getTimestampMillis());
                    context.sendBroadcast(conversationIntent);
                }
            }
        }
    }

    public boolean isSmsReadPermissionGranted() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}