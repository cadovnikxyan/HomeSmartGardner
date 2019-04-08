package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HeatingProcess implements IDBHelper {

    private List<HeatingProcessUnit> unitList;
    private Date startDate = new Date();
    private Date stopDate = new Date();
    private HeatingStatus status = HeatingStatus.HEATING_INPROGRESS;
    private SQLiteDatabase db = null;
    public HeatingProcess(SQLiteDatabase db){
        this.db = db;
        unitList = new ArrayList<>();
    }

    public HeatingProcess(ContentValues values){

    }

    public void start(){
        startDate = Calendar.getInstance().getTime();
    }

    public void stop(){
        stopDate = Calendar.getInstance().getTime();
        storeUnits();
        insert(db, convert());
    }

    public void addUnit(HeatingModes mode, double probe_temp, double temp, Boolean convection, Boolean heating, long id) {
        HeatingProcessUnit unit = new HeatingProcessUnit(mode, probe_temp, temp, convection, heating, Calendar.getInstance().getTime(), id);
        unitList.add(unit);
    }

    private void storeUnits(){
        ContentValues values = new ContentValues(unitList.size());
        for ( HeatingProcessUnit unit : unitList ) {
            ContentValues unitValues = unit.convert();
            long id = unit.insert(db, unitValues);
            values.put(DataContract.HeatingProcessDB.COLUMN_UNIT, id);
        }
        insert(db, values);
    }

    @Override
    public ContentValues convert() {
        ContentValues values = new ContentValues();
        values.put(DataContract.HeatingProcessDB.COLUMN_START_DATE, startDate.toString());
        values.put(DataContract.HeatingProcessDB.COLUMN_STOP_DATE, stopDate.toString());
        values.put(DataContract.HeatingProcessDB.COLUMN_STATUS, status.toString());
        return values;
    }

    @Override
    public long insert(SQLiteDatabase db, ContentValues values) {
        return db.insert(DataContract.HeatingProcessDB.TABLE_NAME, null, values);
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void removeRow(SQLiteDatabase db) {

    }

    public void setStatus(HeatingStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = HeatingStatus.valueOf(status);
    }

    public HeatingStatus getStatus() {
        return status;
    }
}
