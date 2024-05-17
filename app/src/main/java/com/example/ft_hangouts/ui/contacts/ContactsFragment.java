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
    ContactsData contactsData;
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
        contactsData = new ContactsData();

        StoreDataInArrays();

        customAdapter = new CustomAdapter(getContext(), contactsData);
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
                contactsData.addContact(cursor);
            } while (cursor.moveToNext());
        }
    }
}