package com.cadovnik.sausagemakerhelper.data;

import android.database.sqlite.SQLiteDatabase;

public final class DataFactory {

    public static SaltingUnit RestoreSaltingUnit(SQLiteDatabase db, long id){
        return  new SaltingUnit();
    }
    public static HeatingProcessUnit RestoreHeatingProcessUnit(SQLiteDatabase db, long id){
        return new HeatingProcessUnit();
    }
}
