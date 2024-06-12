package com.example.ft_hangouts.ui.contacts.contactPage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.ActivityContactPageBinding;
import com.example.ft_hangouts.permission.PermissionHandler;
import com.example.ft_hangouts.ui.contacts.Contact;
import com.example.ft_hangouts.ui.contacts.addContact.AddContactActivity;
import com.example.ft_hangouts.ui.messages.ConversationActivity;

import java.util.Objects;

public class ContactPageActivity extends AppCompatActivity {

    private static final String TAG = "ContactPageActivity";
    private ActivityContactPageBinding binding;
    Contact contact;
    TextView contactName;
    TextView phoneNumberText;
    TextView addressText;
    TextView cityText;
    TextView postalCodeText;
    TextView emailText;
    ImageView contactImage;
    Button messageButton;
    Button callButton;

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

        contact = new Contact(getIntent());
        if (!contact.isValid()) {
            Log.e(TAG, "Invalid contact id");
            Intent intent = new Intent();
            intent.putExtra("error", true);
            setResult(RESULT_CANCELED, intent);
            finish();
        }

        contactImage = binding.contactPageImage;
        contactName = binding.contactPageName;
        phoneNumberText = binding.contactPagePhoneNumber;
        addressText = binding.contactPageAddress;
        cityText = binding.contactPageCity;
        postalCodeText = binding.contactPagePostalCode;
        emailText = binding.contactPageEmail;
        messageButton = binding.contactMessageButton;
        callButton = binding.contactCallButton;

        setContent();

        binding.contactPageEditButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddContactActivity.class);
            intent.putExtra("id", contact.getContactId());
            intent.putExtra("first_name", contact.getFirstName());
            intent.putExtra("last_name", contact.getLastName());
            intent.putExtra("phone_number", contact.getPhoneNumber());
            intent.putExtra("address", contact.getAddress());
            intent.putExtra("city", contact.getCity());
            intent.putExtra("postal_code", contact.getPostalCode());
            intent.putExtra("email", contact.getEmail());
            intent.putExtra("image_uri", contact.getImagePath());
            startActivity(intent);
        });

        binding.contactPageDeleteButton.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(this);
            String imageUri = contact.getImagePath();

            Intent intent = new Intent();
            intent.putExtra("delete_contact", true);

            int result = db.deleteContact(contact.getContactId());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHandler.PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted");
                callAction(contact.getPhoneNumber());
            } else
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == PermissionHandler.PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted");
                openConversation();
            } else
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void callAction(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper db = new DatabaseHelper(this);
        contact = db.getContact(contact.getContactId());
        if (!contact.isValid())
            finish();
        setContent();
    }

    void setContent() {
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
            callButton.setVisibility(View.VISIBLE);

            if (PhoneNumberUtils.isGlobalPhoneNumber(contact.getPhoneNumber())) {
                messageButton.setOnClickListener(v -> messageCallback());
                callButton.setOnClickListener(v -> callCallback());
            }
            else {
                messageButton.setActivated(false);
                callButton.setActivated(false);
            }
        }
        else {
            phoneNumberText.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
            callButton.setVisibility(View.GONE);
        }

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

        if (!contact.getImagePath().isEmpty())
            contactImage.setImageURI(contact.getImageUri());
        else
            contactImage.setImageResource(R.drawable.default_user);
    }

    private void messageCallback() {
        if (PermissionHandler.isPermissionGranted(this, android.Manifest.permission.READ_SMS))
            openConversation();
        else
            PermissionHandler.requestReadSmsPermission(this);
    }

    private void openConversation() {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("contact_id", contact.getContactId());
        startActivity(intent);
    }

    private void callCallback() {
        if (PermissionHandler.isPermissionGranted(this, android.Manifest.permission.CALL_PHONE))
            callAction(contact.getPhoneNumber());
        else
            PermissionHandler.requestCallPermission(this);
    }
}