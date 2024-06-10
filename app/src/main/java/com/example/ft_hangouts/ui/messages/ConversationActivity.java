package com.example.ft_hangouts.ui.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.ActivityConversationBinding;
import com.example.ft_hangouts.ui.contacts.Contact;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class ConversationActivity extends AppCompatActivity {

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
            Log.e("onCreate: ", "Invalid contact");
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
        if (messagesRV.getAdapter().getItemCount() > 0) {
            messagesRV.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                if (bottom < oldBottom) {
                    messagesRV.postDelayed(() -> messagesRV.smoothScrollToPosition(
                            messagesRV.getAdapter().getItemCount() - 1), 100);
                }
            });
        }

        // Setup toolbar
        CharSequence toolbarTitle = contact.getFullName().isEmpty() ? contact.getPhoneNumber() : contact.getFullName();
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
        sendButton.setOnClickListener(v -> {
            Optional<String> message = Optional.ofNullable(messageInput.getText()).map(CharSequence::toString);
            if (!message.isPresent() || message.get().isEmpty())
                return;

            Message newMessage = sendMessage(message.get());
            db.addMessage(newMessage);
            messageAdapter.addMessage(newMessage);
            messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
            messageInput.setText("");
            messagesRV.scrollToPosition(messageAdapter.getItemCount() - 1);
        });
    }

    private Message sendMessage(String message) {
        Message newMessage = null;

        if (message.contains("/dev")) {
            String[] parts = message.split(" ");
            if (parts.length >= 2) {
                StringBuilder messageToSend = new StringBuilder();
                for (int i = 1; i < parts.length; i++) {
                    messageToSend.append(parts[i]).append(" ");
                    newMessage = new Message(messageToSend.toString(), new Date().getTime(), 0, false, contact.getContactId());
                }
            }
        }
        else {
//            // Send SMS
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(contact.getPhoneNumber(), null, messageInput.getText().toString(), null, null);

            newMessage = new Message(message, 0, new Date().getTime(), true, contact.getContactId());
        }
        return newMessage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}