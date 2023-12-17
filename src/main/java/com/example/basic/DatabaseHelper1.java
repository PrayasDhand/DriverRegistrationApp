package com.example.basic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper1 extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DriversDatabase.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper1(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE Drivers (id INTEGER PRIMARY KEY, name TEXT, email TEXT, password TEXT, contact TEXT)";
        db.execSQL(createTableQuery);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS Drivers";
        db.execSQL(dropTableQuery);
        onCreate(db);
    }
}
