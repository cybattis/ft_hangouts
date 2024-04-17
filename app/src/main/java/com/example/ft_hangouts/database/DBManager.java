package com.example.ft_hangouts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String firstName, String lastName, String phoneNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FIRST_NAME, firstName);
        contentValues.put(DatabaseHelper.LAST_NAME, lastName);
        contentValues.put(DatabaseHelper.PHONE_NUMBER, phoneNumber);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] {
                DatabaseHelper._ID,
                DatabaseHelper.FIRST_NAME,
                DatabaseHelper.LAST_NAME,
                DatabaseHelper.PHONE_NUMBER
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String firstName, String lastName, String phoneNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FIRST_NAME, firstName);
        contentValues.put(DatabaseHelper.LAST_NAME, lastName);
        contentValues.put(DatabaseHelper.PHONE_NUMBER, phoneNumber);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}
