package com.example.ft_hangouts.ui.addContact;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContentResolverCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.ActivityAddContactBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

    // Registers a photo picker activity launcher in single-select mode.
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    contactImageButton.setImageURI(uri);
                    imageFile = uriToBitmap(uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

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
        contactImageButton = binding.contactImage;

        binding.addButtonDb.setOnClickListener(v -> {
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

                String contactImage = "";
                if (imageFile != null)
                    contactImage = copyImage(imageFile);

                Log.d("AddContactActivity", "Contact image: " + contactImage);

                if (!getIntent().hasExtra("id"))
                    db.addContact(firstName, lastName, phoneNumber, address, city, postalCode, email, contactImage);
                else
                    db.update(Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra("id"))),
                            firstName, lastName, phoneNumber, address, city, postalCode, email, contactImage);
                finish();
            } catch (Exception e) {
                Log.d("AddContactActivity", "Error: " + e.getMessage());
            }
        });

        binding.contactImage.setOnClickListener(v -> onSelectNewPhotoButtonClick());

        setUpdateActivity();
    }

    private void onSelectNewPhotoButtonClick() {
        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build();
        pickMedia.launch(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
            contactImageButton.setTag(getIntent().getStringExtra("image_uri"));
        }
    }

    private String copyImage(Bitmap image) {
        try {
            String appDataDir = getApplicationContext().getApplicationInfo().dataDir;
            appDataDir = Files.createDirectories(Paths.get(appDataDir + '/' + "images")).toString();

            String fileName = appDataDir + "/" + UUID.randomUUID().toString() + ".jpg";
            System.out.println("PhotoPicker: Destination: " + fileName);

            try (FileOutputStream out = new FileOutputStream(fileName)) {
                image.compress(Bitmap.CompressFormat.PNG, 90, out);
                return out.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}