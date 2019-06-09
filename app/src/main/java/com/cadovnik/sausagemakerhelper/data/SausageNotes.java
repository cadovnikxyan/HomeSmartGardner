package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SausageNotes implements IDBHelper {
    private List<SausageNote> notes = new ArrayList<>();
    private DataController controller;
    private SausageNotes(){}
    public SausageNotes(@Nullable Context context){
        controller = new DataController(context);
        notes = DataFactory.RestoreSausageNotes(controller.getWritableDatabase());

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
    public long getId() {
        return 0;
    }

    @Override
    public void removeRow(SQLiteDatabase db) {

    }

    public void RemoveAt(int i){
        notes.remove(i);
    }

    public int getCount(){
        return notes.size();
    }
    public SausageNote At(int i){
        return notes.get(i);
    }
}
