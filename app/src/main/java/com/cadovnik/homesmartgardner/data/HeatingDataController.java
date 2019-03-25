package com.cadovnik.homesmartgardner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HeatingDataController extends SQLiteOpenHelper {
    public static final String LOG_TAG = HeatingDataController.class.getSimpleName();
    private static final String DATABASE_NAME = "heating.db";
    private static final int DATABASE_VERSION = 1;

    public HeatingDataController(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
