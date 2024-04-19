package com.example.ft_hangouts.ui.keypad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.ft_hangouts.databinding.FragmentKeypadBinding;

public class KeypadFragment extends Fragment {

    private FragmentKeypadBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        KeypadViewModel keypadViewModel =
                new ViewModelProvider(this).get(KeypadViewModel.class);

        binding = FragmentKeypadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}