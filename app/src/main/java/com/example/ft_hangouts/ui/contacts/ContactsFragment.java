package com.example.ft_hangouts.ui.contacts;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
                    createContactList();
            }
            else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                if (result.getData().getBooleanExtra("delete_contact", false))
                    Toast.makeText(getContext(), "Error when deleting contact", Toast.LENGTH_SHORT).show();
                else if (result.getData().getBooleanExtra("error", false))
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    );

    private final ActivityResultLauncher<Intent> addContactActivity = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getData() == null)
                return;
            if (result.getResultCode() == Activity.RESULT_OK) {
                binding.noContactTextView.setVisibility(View.GONE);
            }
        }
    );

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        contactRV = binding.contactsList;
        binding.newContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddContactActivity.class);
            addContactActivity.launch(intent);
        });

        db = new DatabaseHelper(getActivity());
        createContactList();

        return root;
    }

    private void createContactList() {
        contactsListData = db.getAllContacts();
        if (contactsListData.isEmpty())
            binding.noContactTextView.setVisibility(View.VISIBLE);
        else
            binding.noContactTextView.setVisibility(View.GONE);

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
        requireContext().getApplicationContext().unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        createContactList();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(requireContext().getApplicationContext(), mMessageReceiver, new IntentFilter("smsBroadCast"), ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    //This is the handler that will manager to process the broadcast intent
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            long contactId = intent.getLongExtra("contact_id", -1);
            Log.d("ContactId", String.valueOf(contactId));
            if (contactId == -1)
                return;
            createContactList();
        }
    };
}