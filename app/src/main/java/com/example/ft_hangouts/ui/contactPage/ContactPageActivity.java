package com.example.ft_hangouts.ui.contactPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.databinding.ActivityContactPageBinding;
import com.example.ft_hangouts.ui.addContact.AddContactActivity;
import com.example.ft_hangouts.ui.contacts.ContactsData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ContactPageActivity extends AppCompatActivity {

    private ActivityContactPageBinding binding;

    int contact_id;
    TextView contactName;
    String firstName;
    String lastName;

    TextView lastNameText;
    TextView phoneNumberText;
    TextView addressText;
    TextView cityText;
    TextView postalCodeText;
    TextView emailText;
    ImageView contactImage;

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

        getIntentData();

        binding.contactPageEditButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddContactActivity.class);
            intent.putExtra("id", contact_id);
            intent.putExtra("first_name", firstName);
            intent.putExtra("last_name", lastName);
            intent.putExtra("phone_number", phoneNumberText.getText());
            intent.putExtra("address", addressText.getText());
            intent.putExtra("city", cityText.getText());
            intent.putExtra("postal_code", postalCodeText.getText());
            intent.putExtra("email", emailText.getText());
            intent.putExtra("image_uri", contactImage.toString());
            startActivity(intent);
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

    void getIntentData() {
        if (getIntent().hasExtra("id")) {
            contact_id = getIntent().getIntExtra("id", -1);
        } else {
            Log.e("ContactPageActivity", "No id found");
            finish();
        }

        firstName = getIntent().getStringExtra("first_name") != null ? getIntent().getStringExtra("first_name") : "";
        lastName = getIntent().getStringExtra("last_name") != null ? getIntent().getStringExtra("last_name") : "";

        if (firstName.isEmpty() && lastName.isEmpty())
            contactName.setVisibility(View.GONE);
        else
            contactName.setText(String.format("%s %s", firstName, lastNameText));

        if (getIntent().hasExtra("phone_number"))
            phoneNumberText.setText(getIntent().getStringExtra("phone_number"));
        else
            phoneNumberText.setVisibility(View.GONE);

        String address = getIntent().hasExtra("address") ? getIntent().getStringExtra("address") : "";
        String city = getIntent().hasExtra("city") ? getIntent().getStringExtra("city") : "";
        String postalCode = getIntent().hasExtra("postal_code") ? getIntent().getStringExtra("postal_code") : "";

        if (!address.isEmpty())
            addressText.setText(getIntent().getStringExtra("address"));
        if (!city.isEmpty())
            cityText.setText(getIntent().getStringExtra("city"));
        if (!postalCode.isEmpty())
            postalCodeText.setText(getIntent().getStringExtra("postal_code"));
        if (address.isEmpty() && city.isEmpty() && postalCode.isEmpty()){
            addressText.setVisibility(View.GONE);
            cityText.setVisibility(View.GONE);
            postalCodeText.setVisibility(View.GONE);
        }

        if (getIntent().hasExtra("email")) {
            emailText.setText(getIntent().getStringExtra("email"));
        } else
            emailText.setVisibility(View.GONE);

        if (getIntent().hasExtra("image_uri")) {
            String imageUri = getIntent().getStringExtra("image_uri");
            if (imageUri != null)
                contactImage.setImageURI(Uri.parse(imageUri));
            else
                contactImage.setImageResource(R.drawable.default_user);
        }
    }
}