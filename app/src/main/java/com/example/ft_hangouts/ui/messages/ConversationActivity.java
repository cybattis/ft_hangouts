package com.example.ft_hangouts.ui.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

        db = new DatabaseHelper(this);

        // Fetch contact from intent
        contact = db.getContact(getIntent().getLongExtra("contact_id", -1));
        if (contact == null) {
            Log.e("ConversationActivity", "Contact not found");
            finish();
            return;
        }

        // Fetch messages from database
        ArrayList<Message> messages = db.getMessages(contact.getContact_id());
        messageAdapter = new MessageAdapter(this, messages, contact);

        // Setup recycler view
        messagesRV = binding.conversationMessageList;
        messagesRV.setAdapter(messageAdapter);
        messagesRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        messagesRV.scrollToPosition(messageAdapter.getItemCount() - 1);
        messagesRV.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                messagesRV.postDelayed(() -> messagesRV.smoothScrollToPosition(
                        messagesRV.getAdapter().getItemCount() - 1),100);
            }
        });

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
            String message = messageInput.getText().toString();
            if (message.isEmpty()) {
                return;
            }

//            // Send SMS
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(contact.getPhoneNumber(), null, messageInput.getText().toString(), null, null);

            Message newMessage = new Message(message, null, new Date(), true, contact.getContact_id());
            db.addMessage(newMessage);
            messageAdapter.addMessage(newMessage);
            messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
            messageInput.setText("");
            messagesRV.scrollToPosition(messageAdapter.getItemCount() - 1);
        });
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