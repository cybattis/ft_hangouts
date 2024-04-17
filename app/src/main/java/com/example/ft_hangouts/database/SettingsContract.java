package com.example.ft_hangouts.database;

import android.provider.BaseColumns;

public final class SettingsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SettingsContract() {}

    /* Inner class that defines the table contents */
    public static class Settings implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final Boolean COLUMN_NAME_DARK_MODE = false;
    }
}

