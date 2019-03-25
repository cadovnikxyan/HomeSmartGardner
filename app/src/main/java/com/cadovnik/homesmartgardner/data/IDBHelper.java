package com.cadovnik.homesmartgardner.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public interface IDBHelper {
    public ContentValues convert();
    public long insert(SQLiteDatabase db, ContentValues values);
}
