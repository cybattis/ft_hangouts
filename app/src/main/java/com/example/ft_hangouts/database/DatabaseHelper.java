package com.example.ft_hangouts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ft_hangouts.ui.contacts.Contact;
import com.example.ft_hangouts.ui.messages.ConversationItem;
import com.example.ft_hangouts.ui.messages.Message;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String _ID = "_id";

    // Contact table columns
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String POSTAL_CODE = "postal_code";
    private static final String EMAIL = "email";
    private static final String CONTACT_IMAGE_URI = "image_uri";

    // Message table columns
    private static final String MESSAGE = "message";
    private static final String DATE_RECEIVE = "date_receive";
    private static final String DATE_SEND = "date_send";
    private static final String STATUS = "status";
    private static final String ERROR_CODE = "error_code";
    private static final String IS_ME = "is_me";
    private static final String CONTACT_ID = "contact_id";


    // Database Information
    static final String DB_NAME = "ft_hangouts_contacts.db";

    private final Context context;
    private SQLiteDatabase database;

    // database version
    static final int DB_VERSION = 1;

    // Creating contacts table query
    private static final String CONTACT_TABLE_NAME = "CONTACTS";
    private static final String CREATE_CONTACT_TABLE =
            "CREATE TABLE " + CONTACT_TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FIRST_NAME + " TEXT, "
            + LAST_NAME + " TEXT, "
            + PHONE_NUMBER + " VARCHAR, "
            + ADDRESS + " TEXT, "
            + CITY + " TEXT, "
            + POSTAL_CODE + " TEXT, "
            + EMAIL + " TEXT, "
            + CONTACT_IMAGE_URI + " TEXT NOT NULL);";

    // Creating messages table query
    private static final String MESSAGES_TABLE_NAME = "MESSAGES";
    private static final String CREATE_MESSAGES_TABLE =
            "CREATE TABLE " + MESSAGES_TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MESSAGE + " TEXT, "
            + DATE_RECEIVE + " INTERGER, "
            + DATE_SEND + " INTERGER, "
            + STATUS + " INTEGER, "
            + ERROR_CODE + " INTEGER, "
            + IS_ME + " BOOLEAN, "
            + CONTACT_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + CONTACT_ID + ") REFERENCES " + CONTACT_TABLE_NAME + "(" + _ID + "));";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACT_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
        onCreate(db);
    }

    public void close() {
        database.close();
    }

    // =============================================
    // CONTACTS
    // =============================================

    public void addContact(Contact contact) {
        database = this.getWritableDatabase();

        Log.d("DatabaseHelper", "Adding contact: " + contact);

        ContentValues cv = new ContentValues();
        cv.put(FIRST_NAME, contact.getFirstName());
        cv.put(LAST_NAME, contact.getLastName());
        cv.put(PHONE_NUMBER, contact.getPhoneNumber());
        cv.put(ADDRESS, contact.getAddress());
        cv.put(CITY, contact.getCity());
        cv.put(POSTAL_CODE, contact.getPostalCode());
        cv.put(EMAIL, contact.getEmail());
        cv.put(CONTACT_IMAGE_URI, contact.getImagePath());

        long result = database.insert(CONTACT_TABLE_NAME, null, cv);
        if (result == -1)
            Toast.makeText(context, "Fail to add contact", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show();
    }

    public Cursor fetchAllContact() {
        String[] columns = new String[] {
                DatabaseHelper._ID,
                DatabaseHelper.FIRST_NAME,
                DatabaseHelper.LAST_NAME,
                DatabaseHelper.PHONE_NUMBER,
                DatabaseHelper.ADDRESS,
                DatabaseHelper.CITY,
                DatabaseHelper.POSTAL_CODE,
                DatabaseHelper.EMAIL,
                DatabaseHelper.CONTACT_IMAGE_URI
        };
        database = this.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.CONTACT_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchContact(long _id) {
        String[] columns = new String[] {
                DatabaseHelper._ID,
                DatabaseHelper.FIRST_NAME,
                DatabaseHelper.LAST_NAME,
                DatabaseHelper.PHONE_NUMBER,
                DatabaseHelper.ADDRESS,
                DatabaseHelper.CITY,
                DatabaseHelper.POSTAL_CODE,
                DatabaseHelper.EMAIL,
                DatabaseHelper.CONTACT_IMAGE_URI
        };
        database = this.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.CONTACT_TABLE_NAME, columns, DatabaseHelper._ID + " = " + _id, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Contact getContact(long _id) {
        Cursor cursor = fetchContact(_id);
        if (cursor.getCount() == 0) {
            Log.e("getContact", "Contact not found");
            return new Contact();
        }
        return new Contact(cursor);
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactList = new ArrayList<>();
        Cursor cursor = fetchAllContact();
        for (int i = 0; i < cursor.getCount(); i++) {
            Contact contact = new Contact(cursor);
            contactList.add(contact);
            cursor.moveToNext();
        }
        return contactList;
    }

    public int updateContact(Contact contact) {
        database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FIRST_NAME, contact.getFirstName());
        contentValues.put(DatabaseHelper.LAST_NAME, contact.getLastName());
        contentValues.put(DatabaseHelper.PHONE_NUMBER, contact.getPhoneNumber());
        contentValues.put(DatabaseHelper.ADDRESS, contact.getAddress());
        contentValues.put(DatabaseHelper.CITY, contact.getCity());
        contentValues.put(DatabaseHelper.POSTAL_CODE, contact.getPostalCode());
        contentValues.put(DatabaseHelper.EMAIL, contact.getEmail());
        contentValues.put(DatabaseHelper.CONTACT_IMAGE_URI, contact.getImagePath());

        String clause = DatabaseHelper._ID + " = " + contact.getContactId();
        return database.update(DatabaseHelper.CONTACT_TABLE_NAME, contentValues, clause, null);
    }

    public int deleteContact(long _id) {
        database = this.getWritableDatabase();
        return database.delete(DatabaseHelper.CONTACT_TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

    // =============================================
    // MESSAGES
    // =============================================

    public void addMessage(Message message) {
        database = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MESSAGE, message.getMessage());
        cv.put(DATE_RECEIVE, message.getDateReceiveUnix());
        cv.put(DATE_SEND, message.getDateSendUnix());
        cv.put(STATUS, 0);
        cv.put(ERROR_CODE, 0);
        cv.put(IS_ME, message.isMe() ? 1 : 0);
        cv.put(CONTACT_ID, message.getContactId());

        long result = database.insert(MESSAGES_TABLE_NAME, null, cv);
        if (result == -1)
            Toast.makeText(context, "Fail to add message", Toast.LENGTH_SHORT).show();
    }

    public Cursor fetchMessages(long id) {
        String[] columns = new String[] {
                DatabaseHelper._ID,
                DatabaseHelper.MESSAGE,
                DatabaseHelper.DATE_RECEIVE,
                DatabaseHelper.DATE_SEND,
                DatabaseHelper.STATUS,
                DatabaseHelper.ERROR_CODE,
                DatabaseHelper.IS_ME,
                DatabaseHelper.CONTACT_ID
        };

        database = this.getReadableDatabase();
        Cursor cursor = database.query(
                DatabaseHelper.MESSAGES_TABLE_NAME,
                columns,
                DatabaseHelper.CONTACT_ID + "= ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public ArrayList<Message> getMessages(long contactId) {
        Cursor cursor = fetchMessages(contactId);
        ArrayList<Message> messages = new ArrayList<>();
        if (cursor.getCount() == 0) {
            return messages;
        }
        while (!cursor.isAfterLast()) {
            messages.add(new Message(cursor));
            cursor.moveToNext();
        }
        return messages;
    }

    public String getLastMessage(long contactId) {
        Cursor cursor = fetchMessages(contactId);
        if (cursor.getCount() == 0)
            return "";
        cursor.moveToLast();
        return cursor.getString(1);
    }

    public ArrayList<ConversationItem> getAllConversation() {
        ArrayList<ConversationItem> conversationItems = new ArrayList<>();
        ArrayList<Contact> contacts = getAllContacts();
        Log.d("getAllConversation", "contacts: " + contacts.size());
        for (Contact contact : contacts) {
            String lastMessage = getLastMessage(contact.getContactId());
            if (lastMessage.isEmpty())
                continue;
            conversationItems.add(new ConversationItem(contact, lastMessage));
        }
        return conversationItems;
    }
}
