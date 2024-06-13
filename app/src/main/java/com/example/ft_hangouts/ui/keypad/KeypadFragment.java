package com.example.ft_hangouts.ui.keypad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ft_hangouts.databinding.FragmentKeypadBinding;
import com.example.ft_hangouts.permission.PermissionHandler;

public class KeypadFragment extends Fragment {

    private FragmentKeypadBinding binding;
    TextView phoneNumber;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentKeypadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        phoneNumber = binding.phoneNumberDisplay;
        Button button0 = binding.dial0;
        Button button1 = binding.dial1;
        Button button2 = binding.dial2;
        Button button3 = binding.dial3;
        Button button4 = binding.dial4;
        Button button5 = binding.dial5;
        Button button6 = binding.dial6;
        Button button7 = binding.dial7;
        Button button8 = binding.dial8;
        Button button9 = binding.dial9;
        Button buttonStar = binding.dialStar;
        Button buttonHash = binding.dialHash;
        ImageButton buttonDelete = binding.backSpace;
        Button buttonCall = binding.callButton;

        button0.setOnClickListener(v -> phoneNumber.append("0"));
        button1.setOnClickListener(v -> phoneNumber.append("1"));
        button2.setOnClickListener(v -> phoneNumber.append("2"));
        button3.setOnClickListener(v -> phoneNumber.append("3"));
        button4.setOnClickListener(v -> phoneNumber.append("4"));
        button5.setOnClickListener(v -> phoneNumber.append("5"));
        button6.setOnClickListener(v -> phoneNumber.append("6"));
        button7.setOnClickListener(v -> phoneNumber.append("7"));
        button8.setOnClickListener(v -> phoneNumber.append("8"));
        button9.setOnClickListener(v -> phoneNumber.append("9"));
        buttonStar.setOnClickListener(v -> phoneNumber.append("*"));
        buttonHash.setOnClickListener(v -> phoneNumber.append("#"));
        buttonDelete.setOnClickListener(v -> {
            String number = phoneNumber.getText().toString();
            if (!number.isEmpty())
                phoneNumber.setText(number.substring(0, number.length() - 1));
        });
        buttonCall.setOnClickListener(v -> callCallback());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void callAction(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void callCallback() {
        if (PermissionHandler.isPermissionGranted(getContext(), android.Manifest.permission.CALL_PHONE)) {
            String number = phoneNumber.getText().toString();
            if (!number.isEmpty() && PhoneNumberUtils.isGlobalPhoneNumber(number))
                callAction(phoneNumber.getText().toString());
        }
        else
            PermissionHandler.requestCallPermission(getContext());
    }
}