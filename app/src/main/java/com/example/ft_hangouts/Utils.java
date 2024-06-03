package com.example.ft_hangouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static Bitmap uriToBitmap(Context context, Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            assert parcelFileDescriptor != null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            Log.d("uriToBitmap", e.getMessage(), e);
        }
        return null;
    }

    public static Bitmap resizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean keepRatio) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        if (width == newWidth && height == newHeight)
            return bm;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Log.d("resizedBitmap", "Original image size: " + width + "x" + height);
        Log.d("resizedBitmap", "scaling: " + scaleWidth + "x" + scaleHeight);

        if (keepRatio && newHeight == 0) {
            height = (int)(height * scaleWidth);
            width = newWidth;
        }
        else if (keepRatio && newWidth == 0) {
            width = (int)(width * scaleHeight);
            height = newHeight;
        }
        else {
            width = newWidth;
            height = newHeight;
        }

        Log.d("resizedBitmap", "Resizing image to " + width + "x" + height);

        if (width <= 0 || height <= 0) {
            Log.w("resizedBitmap", "Invalid width or height");
            return null;
        }

        return Bitmap.createScaledBitmap(bm, width, height, true);
    }

    public static int getExifRotation(Context context, Uri imageUri) {
        if (imageUri == null) return 0;

        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
            assert inputStream != null;
            ExifInterface exifInterface = new ExifInterface(inputStream);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            Log.d("getExifRotation", e.getMessage(), e);
            return 0;
        }
    }

    public static Bitmap rotateBitmap(Bitmap imageFile, int orientation) {
        if (orientation == 0) return imageFile;

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(imageFile, 0, 0, imageFile.getWidth(), imageFile.getHeight(), matrix, true);
    }

    public static int getTheme(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        return sharedPref.getInt("theme", R.style.Overlay_purple);
    }
    public static int setTheme(Context context) {
        int theme = getTheme(context);
        context.setTheme(theme);
        return theme;
    }

    public static void removeImage(String image) {
        if (image.isEmpty())
            return;

        File file = new File(image);
        try {
            if (file.exists() && !file.delete())
                Log.w("RemoveImage", "Failed to delete image");
            Log.d("RemoveImage", "Deleted image: " + image);
        } catch (Exception e) {
            Log.w("RemoveImage", "Failed to delete image: " + e.getMessage());
        }
    }
}