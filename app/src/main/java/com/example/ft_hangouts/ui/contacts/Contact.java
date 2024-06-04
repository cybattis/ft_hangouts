package com.example.ft_hangouts.ui.contacts;

import android.content.Intent;
import android.database.Cursor;

import androidx.annotation.Nullable;

public class Contact {
    private long contact_id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String email;
    private String imageUri;

    public Contact(Intent intent) {
        this.contact_id = intent.getLongExtra("id", -1);
        this.firstName = intent.getStringExtra("first_name") != null ? intent.getStringExtra("first_name") : "";
        this.lastName = intent.getStringExtra("last_name") != null ? intent.getStringExtra("last_name") : "";
        this.phoneNumber = intent.getStringExtra("phone_number") != null ? intent.getStringExtra("phone_number") : "";
        this.address = intent.getStringExtra("address") != null ? intent.getStringExtra("address") : "";
        this.city = intent.getStringExtra("city") != null ? intent.getStringExtra("city") : "";
        this.postalCode = intent.getStringExtra("postal_code") != null ? intent.getStringExtra("postal_code") : "";
        this.email = intent.getStringExtra("email") != null ? intent.getStringExtra("email") : "";
        this.imageUri = intent.getStringExtra("image_uri") != null ? intent.getStringExtra("image_uri") : "";
    }

    public Contact(Cursor cursor) {
        this.contact_id = cursor.getLong(0);
        this.firstName = cursor.getString(1);
        this.lastName = cursor.getString(2);
        this.phoneNumber = cursor.getString(3);
        this.address = cursor.getString(4);
        this.city = cursor.getString(5);
        this.postalCode = cursor.getString(6);
        this.email = cursor.getString(7);
        this.imageUri = cursor.getString(8);
    }

    public long getContact_id() {
        return contact_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setFirstName(@Nullable String firstName) {
        if (firstName != null)
            this.firstName = firstName;
    }

    public void setLastName(@Nullable String lastName) {
        if (lastName != null)
            this.lastName = lastName;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        if (phoneNumber != null)
            this.phoneNumber = phoneNumber;
    }

    public void setAddress(@Nullable String address) {
        if (address != null)
            this.address = address;
    }

    public void setCity(@Nullable String city) {
        if (city != null)
            this.city = city;
    }

    public void setPostalCode(@Nullable String postalCode) {
        if (postalCode != null)
            this.postalCode = postalCode;
    }

    public void setEmail(@Nullable String email) {
        if (email != null)
            this.email = email;
    }

    public void setImageUri(@Nullable String imageUri) {
        if (imageUri != null)
            this.imageUri = imageUri;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFullAddress() {
        return address + ", " + city + ", " + postalCode;
    }

    public String getFullContact() {
        return getFullName() + "\n" + phoneNumber + "\n" + getFullAddress() + "\n" + email + "\n" + imageUri;
    }
}
