package com.example.ft_hangouts.ui.contacts.contactPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.ActivityContactPageBinding;
import com.example.ft_hangouts.ui.contacts.addContact.AddContactActivity;
import com.example.ft_hangouts.ui.contacts.Contact;
import com.example.ft_hangouts.ui.messages.ConversationActivity;

import java.util.Objects;

public class ContactPageActivity extends AppCompatActivity {

    private ActivityContactPageBinding binding;
    Contact contact;
    TextView contactName;
    TextView phoneNumberText;
    TextView addressText;
    TextView cityText;
    TextView postalCodeText;
    TextView emailText;
    ImageView contactImage;
    ImageButton messageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.setTheme(this);

        binding = ActivityContactPageBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        binding.contactPageToolbar.myToolbar.setTitle(R.string.text_contact);
        setSupportActionBar(binding.contactPageToolbar.myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contact_page_activity_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contactImage = binding.contactPageImage;
        contactName = binding.contactPageName;
        phoneNumberText = binding.contactPagePhoneNumber;
        addressText = binding.contactPageAddress;
        cityText = binding.contactPageCity;
        postalCodeText = binding.contactPagePostalCode;
        emailText = binding.contactPageEmail;
        messageButton = binding.contactMessageButton;

        contact = new Contact(getIntent());
        if (contact.getContact_id() == -1) {
            Log.e("ContactPageActivity", "No contact ID");
            Intent intent = new Intent();
            intent.putExtra("error", true);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        setContent();

        binding.contactPageEditButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddContactActivity.class);
            intent.putExtra("id", contact.getContact_id());
            intent.putExtra("first_name", contact.getFirstName());
            intent.putExtra("last_name", contact.getLastName());
            intent.putExtra("phone_number", contact.getPhoneNumber());
            intent.putExtra("address", contact.getAddress());
            intent.putExtra("city", contact.getCity());
            intent.putExtra("postal_code", contact.getPostalCode());
            intent.putExtra("email", contact.getEmail());
            intent.putExtra("image_uri", contact.getImageUri());
            startActivity(intent);
        });

        binding.contactPageDeleteButton.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(this);
            String imageUri = contact.getImageUri();

            Intent intent = new Intent();
            intent.putExtra("delete_contact", true);

            int result = db.delete(contact.getContact_id());
            if (result > 0) {
                if (!imageUri.isEmpty())
                    Utils.removeImage(imageUri);
                setResult(RESULT_OK, intent);
            }
            else
                setResult(RESULT_CANCELED, intent);

            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("back", true);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper db = new DatabaseHelper(this);
        contact = db.getContact(contact.getContact_id());
        setContent();
    }

    void setContent() {
        if (contact.getContact_id() == -1) {
            Log.e("ContactPageActivity", "No id found");
            finish();
        }

        Log.d("ContactPageActivity", "Contact : " + contact.getFullContact());

        if (contact.getFirstName().isEmpty() && contact.getLastName().isEmpty())
            contactName.setVisibility(View.GONE);
        else {
            contactName.setText(String.format("%s", contact.getFullName()));
            contactName.setVisibility(View.VISIBLE);
        }

        if (!contact.getPhoneNumber().isEmpty()) {
            phoneNumberText.setText(contact.getPhoneNumber());
            phoneNumberText.setVisibility(View.VISIBLE);
            messageButton.setVisibility(View.VISIBLE);

            if (PhoneNumberUtils.isGlobalPhoneNumber(contact.getPhoneNumber()))
                messageButton.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ConversationActivity.class);
                    intent.putExtra("contact_id", contact.getContact_id());
                    startActivity(intent);
                    finish();
                });
            else
                messageButton.setActivated(false);
        }
        else
            phoneNumberText.setVisibility(View.GONE);

        if (!contact.getAddress().isEmpty()) {
            addressText.setText(contact.getAddress());
            addressText.setVisibility(View.VISIBLE);
        }
        else
            addressText.setVisibility(View.GONE);

        if (!contact.getCity().isEmpty()) {
            cityText.setText(contact.getCity());
            cityText.setVisibility(View.VISIBLE);
        }
        else
            cityText.setVisibility(View.GONE);

        if (!contact.getPostalCode().isEmpty()) {
            postalCodeText.setText(contact.getPostalCode());
            postalCodeText.setVisibility(View.VISIBLE);
        }
        else
            postalCodeText.setVisibility(View.GONE);

        if (contact.getAddress().isEmpty() && contact.getCity().isEmpty() && contact.getPostalCode().isEmpty())
            binding.contactAddressIcon.setVisibility(View.GONE);
        else
            binding.contactAddressIcon.setVisibility(View.VISIBLE);

        if (!contact.getEmail().isEmpty()) {
            emailText.setText(contact.getEmail());
            emailText.setVisibility(View.VISIBLE);
        }
        else
            emailText.setVisibility(View.GONE);

        if (!contact.getImageUri().isEmpty())
            contactImage.setImageURI(Uri.parse(contact.getImageUri()));
        else
            contactImage.setImageResource(R.drawable.default_user);
    }
}