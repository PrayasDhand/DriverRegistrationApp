package com.example.basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Drivers.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "DriversList";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIRST_NAME = "firstname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_CONTACT = "contact";
    private static final String COLUMN_LICENSE_NUMBER = "licensenumber";

    private static final String COLUMN_VEHICLE_TYPE = "vehicletype";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_DOB + " TEXT, " +
                COLUMN_CONTACT + " TEXT, " +
                COLUMN_LICENSE_NUMBER + " TEXT, " +
                COLUMN_VEHICLE_TYPE + " TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(String fullName, String email, String password, String dob, String contact, String licenseNo, String vehicleType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FIRST_NAME, fullName);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_DOB, dob);
        contentValues.put(COLUMN_CONTACT, contact);
        contentValues.put(COLUMN_LICENSE_NUMBER, licenseNo);
        contentValues.put(COLUMN_VEHICLE_TYPE, vehicleType);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }



    public boolean doesUserExist(String ocrFirstName, String ocrLastName, String ocrEmail, String ocrPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_FIRST_NAME + " = ? AND " +
                COLUMN_EMAIL + " = ? AND " +
                COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {ocrFirstName + " " + ocrLastName, ocrEmail, ocrPassword};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean userExists = cursor.getCount() > 0;

        cursor.close();
        return userExists;
    }

}
