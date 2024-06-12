package com.example.ft_hangouts.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHandler {
    public static final int PERMISSION_REQUEST_CALL_PHONE = 1;
    public static final int PERMISSION_REQUEST_READ_SMS = 2;
    public static final int PERMISSION_REQUEST_RECEIVE_SMS = 3;
    public static final int PERMISSION_REQUEST_SEND_SMS = 4;

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission))
                return false;
        }
        return true;
    }

    public static void requestCallPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
    }

    public static void requestReadSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
    }

    public static void requestReceiveSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_RECEIVE_SMS);
    }

    public static void requestSendSmsPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
    }
}
