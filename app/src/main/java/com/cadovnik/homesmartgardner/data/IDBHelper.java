package com.cadovnik.homesmartgardner.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public interface IDBHelper {
    ContentValues convert();
    long insert(SQLiteDatabase db, ContentValues values);
}
