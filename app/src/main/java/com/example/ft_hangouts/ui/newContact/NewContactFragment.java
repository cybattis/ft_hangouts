package com.example.ft_hangouts.ui.newContact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ft_hangouts.databinding.FragmentNewContactBinding;

public class NewContactFragment extends Fragment {

    private FragmentNewContactBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewContactViewModel newContactViewModel =
                new ViewModelProvider(this).get(NewContactViewModel.class);

        binding = FragmentNewContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textView;
        newContactViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}