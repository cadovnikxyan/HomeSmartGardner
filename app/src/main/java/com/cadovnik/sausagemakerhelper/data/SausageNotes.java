package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SausageNotes implements IDBHelper {
    private List<SausageNote> notes = new ArrayList<>();

    public SausageNotes(){

    }

    @Override
    public ContentValues convert() {
        return null;
    }

    @Override
    public long insert(SQLiteDatabase db, ContentValues values) {
        return 0;
    }

    public int getCount(){
        return notes.size();
    }

    public static class SausageNote {
        private String name;
        private SaltingUnit salting;
        private HeatingProcess heating;

        public SausageNote(String name, SaltingUnit salting, HeatingProcess heating){
            this.name = name;
            this.salting = salting;
            this.heating = heating;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public SaltingUnit getSalting() {
            return salting;
        }

        public HeatingProcess getHeating() {
            return heating;
        }
    }
}
