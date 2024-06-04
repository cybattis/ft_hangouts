package com.example.ft_hangouts.ui.messages;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.databinding.ActivityConversationBinding;
import com.example.ft_hangouts.ui.contacts.Contact;

import java.util.Objects;

public class ConversationActivity extends AppCompatActivity {

    private ActivityConversationBinding binding;
    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.setTheme(this);

        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_conversation);

        // Fetch contact from intent
        contact = new Contact(getIntent());

        // Setup toolbar
        String toolbarTitle = contact.getFullName().isEmpty() ? contact.getPhoneNumber() : contact.getFullName();
        binding.conversationActivityToolbar.myToolbar.setTitle(toolbarTitle);
        setSupportActionBar(binding.conversationActivityToolbar.myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conversation_page_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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