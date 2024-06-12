package com.example.ft_hangouts.ui.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.FragmentContactsBinding;
import com.example.ft_hangouts.ui.contacts.addContact.AddContactActivity;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    FragmentContactsBinding binding;
    DatabaseHelper db;
    ContactAdapter contactAdapter;
    ArrayList<Contact> contactsListData;
    RecyclerView contactRV;

    private final ActivityResultLauncher<Intent> contactPageActivity = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getData() == null)
                return;
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData().getBooleanExtra("delete_contact", false))
                    Toast.makeText(getContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
            }
            else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                if (result.getData().getBooleanExtra("delete_contact", false))
                    Toast.makeText(getContext(), "Error when deleting contact", Toast.LENGTH_SHORT).show();
                else if (result.getData().getBooleanExtra("error", false))
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    );

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        contactRV = binding.contactsList;
        binding.noContactTextView.setVisibility(View.GONE);
        binding.newContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddContactActivity.class);
            startActivity(intent);
        });

        db = new DatabaseHelper(getActivity());
        createContactList();

        return root;
    }

    private void createContactList() {
        contactsListData = db.getAllContacts();
        if (contactsListData.isEmpty()) {
            binding.noContactTextView.setVisibility(View.VISIBLE);
            return;
        }

        contactAdapter = new ContactAdapter(getContext(), contactsListData, contactPageActivity);
        binding.contactsList.setAdapter(contactAdapter);
        binding.contactsList.setLayoutManager(new LinearLayoutManager(getContext()));
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
}