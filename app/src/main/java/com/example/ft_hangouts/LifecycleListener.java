package com.example.ft_hangouts;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import javax.security.auth.callback.Callback;

public class LifecycleListener implements DefaultLifecycleObserver {
    private boolean enabled = false;
    public LifecycleListener(Context context, Lifecycle lifecycle, Callback callback) {

    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (enabled) {
            // connect
        }
    }

    public void enable() {
        enabled = true;
        if (lifecycle.getCurrentState().isAtLeast(STARTED)) {
            // connect if not connected
        }
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // disconnect if connected
    }

}
