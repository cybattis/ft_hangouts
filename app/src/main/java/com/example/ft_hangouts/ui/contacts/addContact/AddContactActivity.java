package com.example.ft_hangouts.ui.contacts.addContact;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.ft_hangouts.ui.contacts.Contact;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class AddContactActivity extends AppCompatActivity {

    private ActivityAddContactBinding binding;

    Contact contact;

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
    String oldImage = "";

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

        contact = new Contact(getIntent());
        if (contact.getContactId() >= 0)
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

        setTitle(getResources().getString(R.string.title_update_contact));
        binding.addButtonDb.setText(getResources().getString(R.string.update_button));

        firstNameEditText.setText(contact.getFirstName());
        lastNameEditText.setText(contact.getLastName());
        phoneNumberEditText.setText(contact.getPhoneNumber());
        addressEditText.setText(contact.getAddress());
        cityEditText.setText(contact.getCity());
        postalCodeEditText.setText(contact.getPostalCode());
        emailEditText.setText(contact.getEmail());

        if (!contact.getImagePath().isEmpty()){
            oldImage = contact.getImagePath();
            contactImageButton.setImageURI(Uri.parse(oldImage));
            contactImageButton.setTag(oldImage);
        }
    }

    private void addButtonCallback() {
        try {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            contact.setFirstName(firstNameEditText.getText().toString().trim());
            contact.setLastName(lastNameEditText.getText().toString().trim());
            contact.setPhoneNumber(phoneNumberEditText.getText().toString().trim());
            contact.setAddress(addressEditText.getText().toString().trim());
            contact.setCity(cityEditText.getText().toString().trim());
            contact.setPostalCode(postalCodeEditText.getText().toString().trim());
            contact.setEmail(emailEditText.getText().toString().trim());

            if (imageFile != null)
                contact.setImageUri(copyImage(imageFile));

            if (!contact.isValid()) {
                Log.d("addButtonCallback", "Add contact");
                db.addContact(contact);
            }
            else {
                Log.d("addButtonCallback", "Update contact");
                if (!Objects.equals(contact.getImagePath(), oldImage))
                    Utils.removeImage(oldImage);

                if (db.updateContact(contact) == 0)
                    Log.e("addButtonCallback", "Failed to update contact");
            }
            finish();
        } catch (Exception e) {
            Log.w("addButtonCallback", Objects.requireNonNull(e.getMessage()));
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
                Log.w("copyImage", e.getMessage(), e);
            }
        } catch (IOException e) {
            Log.w("copyImage", e.getMessage(), e);
        }
        return "";
    }
}