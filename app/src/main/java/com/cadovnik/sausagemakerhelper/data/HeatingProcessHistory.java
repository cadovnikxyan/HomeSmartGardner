package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class HeatingProcessHistory implements IDBHelper {

    private SQLiteDatabase db;

    public HeatingProcessHistory(SQLiteDatabase db){
        this.db = db;
    }

    @Override
    public ContentValues convert() {
        return null;
    }

    @Override
    public long insert(SQLiteDatabase db, ContentValues values) {
        return 0;
    }

    @Override
    public int getId() {
        return 0;
    }

    public List<HeatingProcess> restoreUnits(){
        List<HeatingProcess> list = new ArrayList<>();
        String[] columns = new String[2];
        Cursor cursor = db.query(DataContract.HeatingProcessHistoryDB.COLUMN_PROCESSES, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
//                String name = cursor.getString(cursor.getColumnIndex());
//                list.add( DataFactory.RestoreHeatingProcessUnit(db, 0));
                cursor.moveToNext();
            }
        }
        return list;
    }
}
