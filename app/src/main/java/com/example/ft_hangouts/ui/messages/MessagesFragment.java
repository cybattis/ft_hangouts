package com.example.ft_hangouts.ui.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
        Log.d("createConversationList: ", conversationItems.size() + " conversations found");
        conversationAdapter = new ConversationAdapter(getContext(), conversationItems);
        conversationRV.setAdapter(conversationAdapter);
        conversationRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}