package com.example.ft_hangouts;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;

public class LifecycleListener implements DefaultLifecycleObserver {
    Context context;
    boolean isStarting = true;
    Date date;
    SimpleDateFormat dateFormat;

    public LifecycleListener(Context context) {
        this.context = context;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (!isStarting)
            Toast.makeText(context, "The app was set in the background the: " + dateFormat.format(date), Toast.LENGTH_LONG).show();
        isStarting = false;
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        date = new Date();
    }
}
