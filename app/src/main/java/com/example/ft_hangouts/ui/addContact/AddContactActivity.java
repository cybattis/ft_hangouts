package com.example.ft_hangouts.ui.addContact;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.ActivityAddContactBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class AddContactActivity extends AppCompatActivity {

    private ActivityAddContactBinding binding;

    TextInputEditText firstNameEditText;
    TextInputEditText lastNameEditText;
    TextInputEditText phoneNumberEditText;
    TextInputEditText addressEditText;
    TextInputEditText cityEditText;
    TextInputEditText postalCodeEditText;
    TextInputEditText emailEditText;

    ImageButton contactImageButton;
    Bitmap imageFile;
    int orientation;
    String oldImage;

    // Registers a photo picker activity launcher in single-select mode.
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
        registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                contactImageButton.setImageURI(uri);

                orientation = Utils.getExifRotation(getApplicationContext(), uri);
                imageFile = Utils.uriToBitmap(getApplicationContext(), uri);
                if (imageFile == null) {
                    Log.w("PhotoPicker", "Failed to convert URI to bitmap");
                    return;
                }

                imageFile = Utils.resizedBitmap(imageFile, 600, 0, true);
                if (imageFile == null)
                    Log.w("PhotoPicker", "Failed to resize image");
                imageFile = Utils.rotateBitmap(imageFile, orientation);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.setTheme(this);

        binding = ActivityAddContactBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.addContactToolbar.myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_contact_constraint_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        contactImageButton = binding.contactImage;
        contactImageButton.setOnClickListener(v -> onSelectNewPhotoButtonClick());

        firstNameEditText = binding.inputFirstNameText;
        lastNameEditText = binding.inputLastNameText;
        phoneNumberEditText = binding.inputPhoneNumberText;
        addressEditText = binding.inputAddressText;
        cityEditText = binding.inputCityText;
        postalCodeEditText = binding.inputPostalCodeText;
        emailEditText = binding.inputEmailText;

        setUpdateActivity();
        binding.addButtonDb.setOnClickListener(v -> addButtonCallback());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSelectNewPhotoButtonClick() {
        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build();
        pickMedia.launch(request);
    }

    // ========================================================================
    // Update contact
    // ========================================================================
    void setUpdateActivity() {
        if (!getIntent().hasExtra("id"))
            return;

        setTitle(getResources().getString(R.string.title_update_contact));
        binding.addButtonDb.setText(getResources().getString(R.string.update_button));

        if (getIntent().hasExtra("first_name")) {
            firstNameEditText.setText(getIntent().getStringExtra("first_name"));
        }
        if (getIntent().hasExtra("last_name")) {
            lastNameEditText.setText(getIntent().getStringExtra("last_name"));
        }
        if (getIntent().hasExtra("phone_number")) {
            phoneNumberEditText.setText(getIntent().getStringExtra("phone_number"));
        }
        if (getIntent().hasExtra("address")) {
            addressEditText.setText(getIntent().getStringExtra("address"));
        }
        if (getIntent().hasExtra("city")) {
            cityEditText.setText(getIntent().getStringExtra("city"));
        }
        if (getIntent().hasExtra("postal_code")) {
            postalCodeEditText.setText(getIntent().getStringExtra("postal_code"));
        }
        if (getIntent().hasExtra("email")) {
            emailEditText.setText(getIntent().getStringExtra("email"));
        }
        if (getIntent().hasExtra("image_uri")) {
            oldImage = getIntent().getStringExtra("image_uri");
            contactImageButton.setImageURI(Uri.parse(oldImage));
            contactImageButton.setTag(oldImage);
        }
    }

    private void addButtonCallback() {
        try {
            DatabaseHelper db = new DatabaseHelper(AddContactActivity.this);

            String firstName = firstNameEditText.getText() != null ?
                    firstNameEditText.getText().toString().trim() : "";
            String lastName = lastNameEditText.getText() != null ?
                    lastNameEditText.getText().toString().trim() : "";
            String phoneNumber = phoneNumberEditText.getText() != null ?
                    phoneNumberEditText.getText().toString() : "";
            String address = addressEditText.getText() != null ?
                    addressEditText.getText().toString().trim() : "";
            String city = cityEditText.getText() != null ?
                    cityEditText.getText().toString().trim() : "";
            String postalCode = postalCodeEditText.getText() != null ?
                    postalCodeEditText.getText().toString().trim() : "";
            String email = emailEditText.getText() != null ?
                    emailEditText.getText().toString().trim() : "";

            String contactImage = oldImage;
            if (imageFile != null)
                contactImage = copyImage(imageFile);
            Log.d("addButtonCallback", "Contact image: " + contactImage);

            if (!getIntent().hasExtra("id")) {
                db.addContact(firstName, lastName, phoneNumber, address, city, postalCode, email, contactImage);
            }
            else {
                if (!Objects.equals(contactImage, oldImage))
                    removeImage(oldImage);
                db.update(Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra("id"))),
                        firstName, lastName, phoneNumber, address, city, postalCode, email, contactImage);
            }
            finish();
        } catch (Exception e) {
            Log.w("addButtonCallback", "Error: " + e.getMessage());
        }
    }

    // ========================================================================
    // Handle contact image
    // ========================================================================
    private String copyImage(Bitmap image) {
        try {
            String appDataDir = getApplicationContext().getApplicationInfo().dataDir;
            appDataDir = Files.createDirectories(Paths.get(appDataDir + '/' + "images")).toString();

            String fileName = appDataDir + "/" + UUID.randomUUID().toString() + ".jpg";
            Log.d("copyImage", "Destination: " + fileName);

            try (FileOutputStream out = new FileOutputStream(fileName)) {
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                return fileName;
            } catch (IOException e) {
                Log.d("copyImage", e.getMessage(), e);
            }
        } catch (IOException e) {
            Log.d("copyImage", e.getMessage(), e);
        }
        return "";
    }

    private void removeImage(String image) {
        if (image.isEmpty())
            return;

        File file = new File(image);
        try {
            if (file.exists() && !file.delete())
                Log.w("PhotoPicker", "Failed to delete old image");
        } catch (Exception e) {
            Log.w("PhotoPicker", "Failed to delete old image: " + e.getMessage());
        }
    }
}