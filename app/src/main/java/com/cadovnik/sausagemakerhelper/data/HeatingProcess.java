package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HeatingProcess implements IDBHelper {

    private List<HeatingProcessUnit> unitList = new ArrayList<HeatingProcessUnit>();
    private Date startDate = new Date();
    private Date stopDate = new Date();
    private HeatingStatus status = HeatingStatus.HEATING_INPROGRESS;
    private SQLiteDatabase db = null;
    public HeatingProcess(SQLiteDatabase db){
        this.db = db;
    }

    public void start(){
        startDate = Calendar.getInstance().getTime();
    }

    public void stop(){
        stopDate = Calendar.getInstance().getTime();
        storeUnits();
        insert(db, convert());
    }

    public void addUnit(HeatingModes mode, double probe_temp, double temp, Boolean convection, Boolean heating) {
        HeatingProcessUnit unit = new HeatingProcessUnit(mode, probe_temp, temp, convection, heating, Calendar.getInstance().getTime());
        unitList.add(unit);
    }

    private void storeUnits(){
        ContentValues values = new ContentValues(unitList.size());
        for ( HeatingProcessUnit unit : unitList ) {
            ContentValues unitValues = unit.convert();
            long id = unit.insert(db, unitValues);
            values.put(HeatingContract.HeatingProcess.COLUMN_UNIT, id);
        }
        insert(db, values);
    }

    @Override
    public ContentValues convert() {
        ContentValues values = new ContentValues();
        values.put(HeatingContract.HeatingProcess.COLUMN_START_DATE, startDate.toString());
        values.put(HeatingContract.HeatingProcess.COLUMN_STOP_DATE, stopDate.toString());
        values.put(HeatingContract.HeatingProcess.COLUMN_STATUS, status.toString());
        return values;
    }

    @Override
    public long insert(SQLiteDatabase db, ContentValues values) {
        return db.insert(HeatingContract.HeatingProcess.TABLE_NAME, null, values);
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
