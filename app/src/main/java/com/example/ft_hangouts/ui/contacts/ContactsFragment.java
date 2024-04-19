package com.example.ft_hangouts.ui.contacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.FragmentContactsBinding;
import com.example.ft_hangouts.ui.addContact.AddContactActivity;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    FragmentContactsBinding binding;
    DatabaseHelper db;
    ArrayList<String> contact_id, contact_first_name, contact_last_name, contact_phone_number;
    CustomAdapter customAdapter;
    RecyclerView contactList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactsViewModel contactsViewModel =
                new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        contactList = binding.contactList;
        binding.addContactActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddContactActivity.class);
            startActivity(intent);
        });

        db = new DatabaseHelper(getActivity());
        createContactList();

        return root;
    }

    private void createContactList() {
        contact_id = new ArrayList<>();
        contact_first_name = new ArrayList<>();
        contact_last_name = new ArrayList<>();
        contact_phone_number = new ArrayList<>();

        StoreDataInArrays();

        customAdapter = new CustomAdapter(getContext(), contact_id, contact_first_name, contact_last_name, contact_phone_number);
        binding.contactList.setAdapter(customAdapter);
        binding.contactList.setLayoutManager(new LinearLayoutManager(ContactsFragment.this.getContext()));
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        createContactList();
    }

    void StoreDataInArrays() {
        Cursor cursor = db.fetch();
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No contact", Toast.LENGTH_SHORT).show();
        } else {
            do {
                contact_id.add(cursor.getString(0));
                contact_first_name.add(cursor.getString(1));
                contact_last_name.add(cursor.getString(2));
                contact_phone_number.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }
    }
}