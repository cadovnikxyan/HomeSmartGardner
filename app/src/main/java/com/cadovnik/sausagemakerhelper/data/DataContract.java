package com.cadovnik.sausagemakerhelper.data;

import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.Map;

public class DataContract {
    private DataContract(){

    }

    public static final String CREATE_TABLE_FIRST_LINE = "create table %s(";
    public static final String TABLE_COLUMN = "%s %s %s";
    public static final String CREATE_TABLE_END_LINE = ");";

    public static final class HeatingProcessUnitDB implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessUnitDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MODE = "Mode";
        public final static String COLUMN_PROBE_TEMP = "ProbeTemp";
        public final static String COLUMN_TEMP = "Temp";
        public final static String COLUMN_CONVECTION = "Convection";
        public final static String COLUMN_HEATING = "Heating";
        public final static String COLUMN_TIMESTAMP = "Timestamp";
        public final static String COLUMN_HEATING_PROCESS_ID = "HeatingProcessID";

        public final static Map<String,Integer> getIndexes(Cursor cursor){
            Map<String,Integer> indexes = new HashMap<>();
            indexes.put(_ID, cursor.getColumnIndex(_ID));
            indexes.put(COLUMN_MODE, cursor.getColumnIndex(COLUMN_MODE));
            indexes.put(COLUMN_PROBE_TEMP, cursor.getColumnIndex(COLUMN_PROBE_TEMP));
            indexes.put(COLUMN_TEMP, cursor.getColumnIndex(COLUMN_TEMP));
            indexes.put(COLUMN_CONVECTION, cursor.getColumnIndex(COLUMN_CONVECTION));
            indexes.put(COLUMN_HEATING, cursor.getColumnIndex(COLUMN_HEATING));
            indexes.put(COLUMN_TIMESTAMP, cursor.getColumnIndex(COLUMN_TIMESTAMP));
            indexes.put(COLUMN_HEATING_PROCESS_ID, cursor.getColumnIndex(COLUMN_HEATING_PROCESS_ID));
            return indexes;
        }
        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key") + ","
                    + String.format(TABLE_COLUMN, COLUMN_MODE, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_PROBE_TEMP, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_TEMP, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_CONVECTION, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_HEATING, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_TIMESTAMP, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_HEATING_PROCESS_ID, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }

    public static final class HeatingProcessDB implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_UNIT = "Unit";
        public final static String COLUMN_START_DATE = "Start_date";
        public final static String COLUMN_STOP_DATE = "Stop_date";
        public final static String COLUMN_STATUS = "Status";

        public final static Map<String,Integer> getIndexes(Cursor cursor){
            Map<String,Integer> indexes = new HashMap<>();
            indexes.put(_ID, cursor.getColumnIndex(_ID));
            indexes.put(COLUMN_UNIT, cursor.getColumnIndex(COLUMN_UNIT));
            indexes.put(COLUMN_START_DATE, cursor.getColumnIndex(COLUMN_START_DATE));
            indexes.put(COLUMN_STOP_DATE, cursor.getColumnIndex(COLUMN_STOP_DATE));
            indexes.put(COLUMN_STATUS, cursor.getColumnIndex(COLUMN_STATUS));
            return indexes;
        }

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key") + ","
                    + String.format(TABLE_COLUMN, COLUMN_UNIT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_START_DATE, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_STOP_DATE, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_STATUS, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }

    public static final class HeatingProcessHistoryDB implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessHistoryDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PROCESSES = "HeatingProcesses";
        public final static String COLUMN_DATE = "Date";

        public final static Map<String,Integer> getIndexes(Cursor cursor){
            Map<String,Integer> indexes = new HashMap<>();
            indexes.put(_ID, cursor.getColumnIndex(_ID));
            indexes.put(COLUMN_PROCESSES, cursor.getColumnIndex(COLUMN_PROCESSES));
            indexes.put(COLUMN_DATE, cursor.getColumnIndex(COLUMN_DATE));
            return indexes;
        }
        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key") + ","
                    + String.format(TABLE_COLUMN, COLUMN_PROCESSES, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_DATE, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }

    public static  final class SaltingUnitDB implements BaseColumns{
        public final static String TABLE_NAME = "SaltingUnitDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SAUSAGE_NAME = "SausageName";
        public final static String COLUMN_MEAT_WEIGHT = "MeatWeight";
        public final static String COLUMN_ROCK_SALT_WEIGHT = "RockSaltWeight";
        public final static String COLUMN_NITRITE_SALT_WEIGHT = "NitriteSaltWeight";
        public final static String COLUMN_PHOSPHATES_WEIGHT = "PhosphatesWeight";
        public final static String COLUMN_SODIUM_ASCORBATE_WEIGHT = "SodiumAscorbateWeight";
        public final static String COLUMN_BRINE_WEIGHT = "BrineWeight";
        public final static String COLUMN_DATE = "Date";

        public final static Map<String,Integer> getIndexes(Cursor cursor){
            Map<String,Integer> indexes = new HashMap<>();
            indexes.put(_ID, cursor.getColumnIndex(_ID));
            indexes.put(COLUMN_SAUSAGE_NAME, cursor.getColumnIndex(COLUMN_SAUSAGE_NAME));
            indexes.put(COLUMN_MEAT_WEIGHT, cursor.getColumnIndex(COLUMN_MEAT_WEIGHT));
            indexes.put(COLUMN_ROCK_SALT_WEIGHT, cursor.getColumnIndex(COLUMN_ROCK_SALT_WEIGHT));
            indexes.put(COLUMN_NITRITE_SALT_WEIGHT, cursor.getColumnIndex(COLUMN_NITRITE_SALT_WEIGHT));
            indexes.put(COLUMN_PHOSPHATES_WEIGHT, cursor.getColumnIndex(COLUMN_PHOSPHATES_WEIGHT));
            indexes.put(COLUMN_SODIUM_ASCORBATE_WEIGHT, cursor.getColumnIndex(COLUMN_SODIUM_ASCORBATE_WEIGHT));
            indexes.put(COLUMN_BRINE_WEIGHT, cursor.getColumnIndex(COLUMN_BRINE_WEIGHT));
            indexes.put(COLUMN_DATE, cursor.getColumnIndex(COLUMN_DATE));
            return indexes;
        }

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SAUSAGE_NAME, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_MEAT_WEIGHT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_ROCK_SALT_WEIGHT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_NITRITE_SALT_WEIGHT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_PHOSPHATES_WEIGHT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SODIUM_ASCORBATE_WEIGHT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_BRINE_WEIGHT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_DATE, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }

    public static final class SaltingExtrasDB implements BaseColumns{
        public final static String TABLE_NAME = "SaltingExtrasDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SALTING_EXTRAS_NAME = "SaltingExtrasName";
        public final static String COLUMN_SALTING_EXTRAS_APPLICATION_RATE = "SaltingExtrasApplicationRate";
        public final static String COLUMN_SALTING_EXTRAS_WEIGHT = "SaltingExtrasWeight";

        public final static Map<String,Integer> getIndexes(Cursor cursor){
            Map<String,Integer> indexes = new HashMap<>();
            indexes.put(_ID, cursor.getColumnIndex(_ID));
            indexes.put(COLUMN_SALTING_EXTRAS_NAME, cursor.getColumnIndex(COLUMN_SALTING_EXTRAS_NAME));
            indexes.put(COLUMN_SALTING_EXTRAS_APPLICATION_RATE, cursor.getColumnIndex(COLUMN_SALTING_EXTRAS_APPLICATION_RATE));
            indexes.put(COLUMN_SALTING_EXTRAS_WEIGHT, cursor.getColumnIndex(COLUMN_SALTING_EXTRAS_WEIGHT));

            return indexes;
        }

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SALTING_EXTRAS_NAME, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SALTING_EXTRAS_APPLICATION_RATE, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SALTING_EXTRAS_WEIGHT, "text", "") + ","
                    + CREATE_TABLE_END_LINE;
        }
    }

    public static  final class SaltingHistoryDB implements BaseColumns{
        public final static String TABLE_NAME = "SaltingHistoryDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SALTINGS = "Saltings";

        public final static Map<String,Integer> getIndexes(Cursor cursor){
            Map<String,Integer> indexes = new HashMap<>();
            indexes.put(_ID, cursor.getColumnIndex(_ID));
            indexes.put(COLUMN_SALTINGS, cursor.getColumnIndex(COLUMN_SALTINGS));
            return indexes;
        }

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SALTINGS, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }

    public static  final class SausageNoteDB implements BaseColumns{
        public final static String TABLE_NAME = "SausageNoteDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SAUSAGE_NAME = "SausageName";
        public final static String COLUMN_SALTING_UNIT = "SaltingUnitID";
        public final static String COLUMN_SAUSAGE_DESCRIPTION = "SausageDescription";
        public final static String COLUMN_HEATING_PROCESS = "HeatingProcess";
        public final static String COLUMN_SAUSAGE_IMAGE = "SausageImage";
        public final static String COLUMN_SAUSAGE_SPICE = "SausageSpice";

        public final static Map<String,Integer> getIndexes(Cursor cursor){
            Map<String,Integer> indexes = new HashMap<>();
            indexes.put(_ID, cursor.getColumnIndex(_ID));
            indexes.put(COLUMN_SAUSAGE_NAME, cursor.getColumnIndex(COLUMN_SAUSAGE_NAME));
            indexes.put(COLUMN_SALTING_UNIT, cursor.getColumnIndex(COLUMN_SALTING_UNIT));
            indexes.put(COLUMN_SAUSAGE_DESCRIPTION, cursor.getColumnIndex(COLUMN_SAUSAGE_DESCRIPTION));
            indexes.put(COLUMN_HEATING_PROCESS, cursor.getColumnIndex(COLUMN_HEATING_PROCESS));
            indexes.put(COLUMN_SAUSAGE_IMAGE, cursor.getColumnIndex(COLUMN_SAUSAGE_IMAGE));
            indexes.put(COLUMN_SAUSAGE_SPICE, cursor.getColumnIndex(COLUMN_SAUSAGE_SPICE));
            return indexes;
        }

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SAUSAGE_NAME, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SALTING_UNIT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SAUSAGE_DESCRIPTION, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_HEATING_PROCESS, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SAUSAGE_IMAGE, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SAUSAGE_SPICE, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }
}
