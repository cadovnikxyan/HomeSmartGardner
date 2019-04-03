package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class HeatingProcessUnit implements IDBHelper {
    private HeatingModes mode = HeatingModes.HEATING_MANUALLY;
    private double probe_temp = -274.0;
    private double temp = -127.0;
    private Boolean convection = false;
    private Boolean heating = false;
    private Date date;
    private long DBTableId = -1;

    public HeatingProcessUnit(HeatingModes mode, double probe_temp, double temp, Boolean convection, Boolean heating, Date date){
        this.mode = mode;
        this.probe_temp = probe_temp;
        this.temp = temp;
        this.convection = convection;
        this.heating = heating;
        this.date = date;
    }
    public HeatingProcessUnit(){
        
    }

    public String getMode() {
        return mode.toString();
    }
    public  void setMode(String mode){
        this.mode = HeatingModes.valueOf(mode);
    }
    public  void setMode(HeatingModes mode){
        this.mode = mode;
    }

    public void setProbeTemp(double probe_temp) {
        this.probe_temp = probe_temp;
    }

    public double getProbeTemp() {
        return probe_temp;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp){
        this.temp = temp;
    }

    public void setConvection(Boolean convection) {
        this.convection = convection;
    }

    public Boolean getConvection() {
        return convection;
    }

    public void setHeating(Boolean heating) {
        this.heating = heating;
    }

    public Boolean getHeating() {
        return heating;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public ContentValues convert() {
        ContentValues values = new ContentValues();
        values.put(DataContract.HeatingProcessUnitDB.COLUMN_MODE, getMode());
        values.put(DataContract.HeatingProcessUnitDB.COLUMN_TEMP, temp);
        values.put(DataContract.HeatingProcessUnitDB.COLUMN_PROBE_TEMP, probe_temp);
        values.put(DataContract.HeatingProcessUnitDB.COLUMN_HEATING, heating);
        values.put(DataContract.HeatingProcessUnitDB.COLUMN_CONVECTION, convection);
        values.put(DataContract.HeatingProcessUnitDB.COLUMN_TIMESTAMP, date.toString());
        return values;
    }

    @Override
    public long insert(SQLiteDatabase db, ContentValues values) {
        DBTableId = db.insert(DataContract.HeatingProcessUnitDB.TABLE_NAME, null, values);
        return DBTableId;
    }

}
