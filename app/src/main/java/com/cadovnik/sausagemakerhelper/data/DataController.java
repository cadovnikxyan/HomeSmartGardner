package com.cadovnik.sausagemakerhelper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataController extends SQLiteOpenHelper {
    public static final String LOG_TAG = DataController.class.getSimpleName();
    private static final String DATABASE_NAME = "sausagemakerhelper.db";
    private static final int DATABASE_VERSION = 1;

    public DataController(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    @Override
    public void onOpen(SQLiteDatabase db){

    }
}
