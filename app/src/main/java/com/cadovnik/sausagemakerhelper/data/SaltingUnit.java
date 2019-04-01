package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaltingUnit implements IDBHelper  {

    private final double sodium_ascorbate_percent = 0.0005;
    private final double phosphates_percent = 0.003;
    private final double brine_percent = 0.1;

    private double weight_of_meat = 0.0;
    private double salting_percent = 0.0;
    private double nitrite_salting_percent = 0.0;
    private double rock_salt = 0.0;
    private double nitrite_salt = 0.0;
    private double phosphates = 0.0;
    private double sodium_ascorbate = 0.0;
    private double brine = 0.0;
    private boolean wet_salting = false;
    private boolean with_phosphates = false;
    private boolean with_sodium_ascorbate = false;
    private String sausage_name = "";
    private Date date = new Date();
    private long DBTableId;

    public SaltingUnit(){

    }

    public List<String> calculate(){
        List<String> results = new ArrayList<>();

        rock_salt = weight_of_meat * ( salting_percent / 100 );

        if ( wet_salting ){
            brine = weight_of_meat * brine_percent;
            rock_salt = (weight_of_meat + brine) * (salting_percent / 100);
            results.add(String.format("Brine weight: %f", brine));
        }
        if ( nitrite_salting_percent != 0 ) {
            nitrite_salt = rock_salt * (nitrite_salting_percent / 100);
            rock_salt = rock_salt - nitrite_salt;
            results.add(String.format("Nitrite salt weight: %f", nitrite_salt));
        }
        if ( with_phosphates ) {
            phosphates = weight_of_meat * phosphates_percent;
            results.add(String.format("Phosphates weight: %f", phosphates));
        }
        if ( with_sodium_ascorbate ) {
            sodium_ascorbate = weight_of_meat * sodium_ascorbate_percent;
            results.add(String.format("Sodium ascorbate weight: %f", sodium_ascorbate));
        }

        results.add(String.format("Rock salt weight: %f", rock_salt));
        return results;
    }

    @Override
    public ContentValues convert() {
        ContentValues values = new ContentValues();
        values.put(DataContract.SaltingUnitDB.COLUMN_BRINE_WEIGHT, brine);
        values.put(DataContract.SaltingUnitDB.COLUMN_MEAT_WEIGHT, weight_of_meat);
        values.put(DataContract.SaltingUnitDB.COLUMN_ROCK_SALT_WEIGHT, rock_salt);
        values.put(DataContract.SaltingUnitDB.COLUMN_NITRITE_SALT_WEIGHT, nitrite_salt);
        values.put(DataContract.SaltingUnitDB.COLUMN_PHOSPHATES_WEIGHT, phosphates);
        values.put(DataContract.SaltingUnitDB.COLUMN_SODIUM_ASCORBATE_WEIGHT, sodium_ascorbate);
        values.put(DataContract.SaltingUnitDB.COLUMN_SAUSAGE_NAME, sausage_name);
        values.put(DataContract.SaltingUnitDB.COLUMN_DATE, date.toString());
        return null;
    }

    @Override
    public long insert(SQLiteDatabase db, ContentValues values) {
        DBTableId = db.insert(DataContract.SaltingUnitDB.TABLE_NAME, null, values);
        return DBTableId;
    }

    public void setWeight_of_meat(double weight_of_meat) {
        this.weight_of_meat = weight_of_meat;
    }

    public double getWeight_of_meat() {
        return weight_of_meat;
    }

    public void setSalting_percent(double salting_percent) {
        this.salting_percent = salting_percent;
    }

    public double getSalting_percent() {
        return salting_percent;
    }

    public void setNitrite_salting_percent(double nitrite_salting_percent) {
        this.nitrite_salting_percent = nitrite_salting_percent;
    }

    public double getNitrite_salting_percent() {
        return nitrite_salting_percent;
    }

    public void setRock_salt(double rock_salt) {
        this.rock_salt = rock_salt;
    }

    public double getRock_salt() {
        return rock_salt;
    }

    public void setNitrite_salt(double nitrite_salt) {
        this.nitrite_salt = nitrite_salt;
    }

    public double getNitrite_salt() {
        return nitrite_salt;
    }

    public void setPhosphates(double phosphates) {
        this.phosphates = phosphates;
    }

    public double getPhosphates() {
        return phosphates;
    }

    public void setSodium_ascorbate(double sodium_ascorbate) {
        this.sodium_ascorbate = sodium_ascorbate;
    }

    public double getSodium_ascorbate() {
        return sodium_ascorbate;
    }

    public void setBrine(double brine) {
        this.brine = brine;
    }

    public double getBrine() {
        return brine;
    }

    public void setWet_salting(boolean wet_salting) {
        this.wet_salting = wet_salting;
    }

    public boolean isWet_salting() {
        return wet_salting;
    }

    public void setWith_phosphates(boolean with_phosphates) {
        this.with_phosphates = with_phosphates;
    }

    public boolean isWith_phosphates() {
        return with_phosphates;
    }

    public void setWith_sodium_ascorbate(boolean with_sodium_ascorbate) {
        this.with_sodium_ascorbate = with_sodium_ascorbate;
    }

    public boolean isWith_sodium_ascorbate() {
        return with_sodium_ascorbate;
    }

    public void setSausage_name(String sausage_name) {
        this.sausage_name = sausage_name;
    }

    public String getSausage_name() {
        return sausage_name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
