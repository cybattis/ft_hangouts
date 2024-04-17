package com.example.ft_hangouts.ui.newContact;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewContactViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewContactViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is new contact fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}