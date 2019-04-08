package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DataFactory {

    public static SaltingUnit RestoreSaltingUnit(SQLiteDatabase db, int id){
        return  new SaltingUnit();
    }

    public static HeatingProcessUnit RestoreHeatingProcessUnit(SQLiteDatabase db, int id){
        return new HeatingProcessUnit();
    }

    public static List<SausageNote> RestoreSausageNotes(SQLiteDatabase db){
        List<SausageNote> notes = new ArrayList<>();
        Cursor cursorSausageNotes = db.query(DataContract.SausageNoteDB.TABLE_NAME, null, null, null, null, null, null);
        Cursor cursorSalting = db.query(DataContract.SaltingUnitDB.TABLE_NAME, null, null, null, null, null, null);
//        Cursor cursorHeating = db.query(DataContract.HeatingProcessDB.TABLE_NAME, null, null, null, null, null, null);
        if ( cursorSausageNotes.moveToFirst() && cursorSalting.moveToFirst()){

                Map<String, Integer> indexesSalting = DataContract.SaltingUnitDB.getIndexes(cursorSalting);
                Map<String, Integer> indexesSausage = DataContract.SausageNoteDB.getIndexes(cursorSausageNotes);
                do{
                    try{
                        ContentValues values = new ContentValues();
                        indexesSalting.forEach((s, integer) -> values.put(s, cursorSalting.getString(integer)));
                        indexesSausage.forEach((s, integer) -> {
                            if (s.equals(DataContract.SausageNoteDB.COLUMN_SAUSAGE_IMAGE))
                                values.put(s, cursorSausageNotes.getBlob(integer));
                            else
                                values.put(s, cursorSausageNotes.getString(integer));
                        });
                        notes.add(new SausageNote(values));
                    }
                    catch (SQLException e){
                                Log.e(e.getClass().toString(), "Error: " + e.toString());
                            }
                    catch (CursorIndexOutOfBoundsException ex){
                                Log.e(ex.getClass().toString(), "Error: " + ex.toString());
                            }
            }while (cursorSausageNotes.moveToNext() && cursorSalting.moveToNext());
        }
        return notes;
    }

}
