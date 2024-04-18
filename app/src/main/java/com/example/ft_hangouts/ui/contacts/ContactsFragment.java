package com.example.ft_hangouts.ui.contacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.database.DBManager;
import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.FragmentContactsBinding;
import com.example.ft_hangouts.ui.newContact.NewContactFragment;

public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private DBManager dbManager;
    private ListView listView;
    private SimpleCursorAdapter adapter;

    final String[] from = new String[] {
            DatabaseHelper._ID,
            DatabaseHelper.FIRST_NAME,
            DatabaseHelper.LAST_NAME,
            DatabaseHelper.PHONE_NUMBER
    };

    final int[] to = new int[] { R.id.input_first_name, R.id.input_last_name, R.id.input_phone_number };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactsViewModel contactsViewModel =
                new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbManager = new DBManager(this.getContext());
        dbManager.open();

        Cursor cursor = dbManager.fetch();

        listView = (ListView) listView.findViewById(R.id.contacts_list);
        listView.setEmptyView(listView.findViewById(R.id.empty_list_text));

        //.......

        final TextView textView = binding.emptyListText;
        contactsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addContactButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(ContactsFragment.this)
                    .navigate(R.id.navigation_new_contact);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}