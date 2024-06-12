package com.example.ft_hangouts.ui.messages;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.ActivityConversationBinding;
import com.example.ft_hangouts.databinding.FragmentMessagesBinding;
import com.example.ft_hangouts.ui.contacts.Contact;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class ConversationActivity extends AppCompatActivity {
    private static final String TAG = "ConversationActivity";
    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    private static final int PERMISSION_REQUEST_READ_SMS = 124;
    private ActivityConversationBinding binding;
    private DatabaseHelper db;
    Contact contact;
    Button sendButton;
    MessageAdapter messageAdapter;
    TextInputEditText messageInput;
    RecyclerView messagesRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.setTheme(this);

        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(getApplicationContext());

        // Fetch contact from intent
        contact = db.getContact(getIntent().getLongExtra("contact_id", -1));
        if (!contact.isValid()) {
            Log.e(TAG, "Invalid contact");
            finish();
        }

        // Fetch messages from database
        ArrayList<Message> messages = db.getMessages(contact.getContactId());
        messageAdapter = new MessageAdapter(this, messages, contact);

        // Setup recycler view
        messagesRV = binding.conversationMessageList;
        messagesRV.setAdapter(messageAdapter);
        messagesRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        messagesRV.scrollToPosition(messageAdapter.getItemCount() - 1);
        if (messagesRV.getAdapter() != null && messagesRV.getAdapter().getItemCount() > 0) {
            messagesRV.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                if (bottom < oldBottom) {
                    messagesRV.postDelayed(() -> messagesRV.smoothScrollToPosition(
                            messagesRV.getAdapter().getItemCount() - 1), 100);
                }
            });
        }

        // Setup toolbar
        CharSequence toolbarTitle = contact.hasName() ? contact.getFullName() : contact.getPhoneNumber();
        binding.conversationActivityToolbar.myToolbar.setTitle(toolbarTitle);
        setSupportActionBar(binding.conversationActivityToolbar.myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conversation_page_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup components
        messageInput = binding.inputMessage;
        sendButton = binding.sendMessageButton;
        sendButton.setOnClickListener(v -> sendSms());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isSmsSendPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private Boolean sendSms() {
        Optional<String> message = Optional.ofNullable(messageInput.getText()).map(CharSequence::toString);
        if (!message.isPresent() || message.get().isEmpty())
            return false;

        if (!isSmsSendPermissionGranted()) {
            Log.d("sendSms: ", "Requesting permission to send sms");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
        else {
            SmsManager smsManager = SmsManager.getDefault();
            try {
                Log.d("sendSms: ", "Sending sms to " + contact.getPhoneNumber());
                smsManager.sendTextMessage(contact.getPhoneNumber(), null, message.get(), null, null);
                Message newMessage = new Message(message.get(), 0, new Date().getTime(), true, contact.getContactId());
                db.addMessage(newMessage);
                messageAdapter.addMessage(newMessage);
                messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
                messageInput.setText("");
                messagesRV.scrollToPosition(messageAdapter.getItemCount() - 1);
                return true;
            } catch (Exception e) {
                Log.e("sendSms: ", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(getApplicationContext(), "SMS failed", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (sendSms())
                    Log.i(TAG, "SMS send successfully");
            }
            else
                Toast.makeText(getApplicationContext(), "No permission to send sms.", Toast.LENGTH_LONG).show();
        }
    }

    //register your activity onResume()
    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(mMessageReceiver, new IntentFilter("smsBroadCast"));
    }

    //Must unregister onPause()
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            long contactId = intent.getLongExtra("contact_id", -1);
            long timestamp = intent.getLongExtra("timestamp", 0);

            //do other stuff here
            if (contactId == contact.getContactId()) {
                Message newMessage = new Message(message, timestamp, 0, false, contact.getContactId());
                db.addMessage(newMessage);
                messageAdapter.addMessage(newMessage);
                messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
                messagesRV.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        }
    };
}