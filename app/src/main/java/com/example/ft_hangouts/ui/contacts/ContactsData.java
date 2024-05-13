package com.example.ft_hangouts.ui.contacts;

import android.database.Cursor;

import java.util.ArrayList;

public class ContactsData {
    ArrayList<String> contact_id;
    ArrayList<String> contact_first_name;
    ArrayList<String> contact_last_name;
    ArrayList<String> contact_phone_number;
    ArrayList<String> contact_address;
    ArrayList<String> contact_city;
    ArrayList<String> contact_postal_code;
    ArrayList<String> contact_email;
    ArrayList<String> contact_image_uri;

    public ContactsData() {
        contact_id = new ArrayList<>();
        contact_first_name = new ArrayList<>();
        contact_last_name = new ArrayList<>();
        contact_phone_number = new ArrayList<>();
        contact_address = new ArrayList<>();
        contact_city = new ArrayList<>();
        contact_postal_code = new ArrayList<>();
        contact_email = new ArrayList<>();
        contact_image_uri = new ArrayList<>();
    }

    public void addContact(Cursor cursor) {
        contact_id.add(cursor.getString(0));
        contact_first_name.add(cursor.getString(1));
        contact_last_name.add(cursor.getString(2));
        contact_phone_number.add(cursor.getString(3));
        contact_address.add(cursor.getString(4));
        contact_city.add(cursor.getString(5));
        contact_postal_code.add(cursor.getString(6));
        contact_email.add(cursor.getString(7));
        contact_image_uri.add(cursor.getString(8));
    }
}
