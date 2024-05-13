package com.example.ft_hangouts.ui.addContact;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.ActivityAddContactBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AddContactActivity extends AppCompatActivity {

    ActivityAddContactBinding binding;

    TextInputEditText firstNameEditText;
    TextInputEditText lastNameEditText;
    TextInputEditText phoneNumberEditText;
    TextInputEditText addressEditText;
    TextInputEditText cityEditText;
    TextInputEditText postalCodeEditText;
    TextInputEditText emailEditText;
    ImageButton contactImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_contact_constraint_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Add Up navigation button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firstNameEditText = binding.inputFirstNameText;
        lastNameEditText = binding.inputLastNameText;
        phoneNumberEditText = binding.inputPhoneNumberText;
        addressEditText = binding.inputAddressText;
        cityEditText = binding.inputCityText;
        postalCodeEditText = binding.inputPostalCodeText;
        emailEditText = binding.inputEmailText;
        contactImage = binding.contactImage;


        binding.addButtonDb.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(AddContactActivity.this);
            db.addContact(firstNameEditText.getText().toString().trim(),
                          lastNameEditText.getText().toString().trim(),
                          phoneNumberEditText.getText().toString().trim(),
                          addressEditText.getText().toString().trim(),
                          cityEditText.getText().toString().trim(),
                          postalCodeEditText.getText().toString().trim(),
                          emailEditText.getText().toString().trim(),
                          contactImage.getTag().toString());
            finish();
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