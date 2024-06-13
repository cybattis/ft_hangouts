package com.example.ft_hangouts.ui.messages;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.database.DatabaseHelper;
import com.example.ft_hangouts.databinding.FragmentMessagesBinding;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;
    DatabaseHelper db;
    ConversationAdapter conversationAdapter;
    ArrayList<ConversationItem> conversationItems;
    RecyclerView conversationRV;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        conversationRV = binding.messagesList;

        db = new DatabaseHelper(getActivity());
        createConversationList();

        return root;
    }

    private void createConversationList() {
        conversationItems = db.getAllConversations();
        conversationAdapter = new ConversationAdapter(getContext(), conversationItems);
        conversationRV.setAdapter(conversationAdapter);
        conversationRV.setLayoutManager(new LinearLayoutManager(getContext()));
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
            createConversationList();
        }
    };
}