package com.cadovnik.sausagemakerhelper.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DataController extends SQLiteOpenHelper {
    public static final String LOG_TAG = DataController.class.getSimpleName();
    private static final String DATABASE_NAME = "sausagemakerhelper.db";
    private static final int DATABASE_VERSION = 14;

    public DataController(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        createTable(db, DataContract.HeatingProcessDB.createTableString());
        createTable(db, DataContract.HeatingProcessUnitDB.createTableString());
        createTable(db, DataContract.HeatingProcessHistoryDB.createTableString());
        createTable(db, DataContract.SaltingHistoryDB.createTableString());
        createTable(db, DataContract.SaltingUnitDB.createTableString());
        createTable(db, DataContract.SausageNoteDB.createTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  DataContract.HeatingProcessDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +  DataContract.HeatingProcessUnitDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +  DataContract.HeatingProcessHistoryDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +  DataContract.SaltingHistoryDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +  DataContract.SaltingUnitDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +  DataContract.SausageNoteDB.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){

    }

    private void createTable(SQLiteDatabase db, String table){
        try{
            db.execSQL(table);
        }catch (SQLException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }

    }
}
