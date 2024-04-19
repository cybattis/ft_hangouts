package com.example.ft_hangouts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "CONTACTS";

    // Table columns
    private static final String _ID = "_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PHONE_NUMBER = "phone_number";

    // Database Information
    static final String DB_NAME = "ft_hangouts_contacts.db";

    private final Context context;
    private SQLiteDatabase database;

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FIRST_NAME + " TEXT, "
            + LAST_NAME + " TEXT, "
            + PHONE_NUMBER + " PHONE_NUMBER);";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void close() {
        database.close();
    }

    public void addContact(@Nullable String firstName, @Nullable String lastName, @Nullable String phoneNumber) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FIRST_NAME, firstName);
        cv.put(LAST_NAME, lastName);
        cv.put(PHONE_NUMBER, phoneNumber);
        long result = database.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Fail to add contact", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor fetch() {
        String[] columns = new String[] {
                DatabaseHelper._ID,
                DatabaseHelper.FIRST_NAME,
                DatabaseHelper.LAST_NAME,
                DatabaseHelper.PHONE_NUMBER
        };
        database = this.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String firstName, String lastName, String phoneNumber) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FIRST_NAME, firstName);
        contentValues.put(DatabaseHelper.LAST_NAME, lastName);
        contentValues.put(DatabaseHelper.PHONE_NUMBER, phoneNumber);
        return database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
    }

    public void delete(long _id) {
        database = this.getWritableDatabase();
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}
