package com.example.ft_hangouts.ui.keypad;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KeypadViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public KeypadViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}