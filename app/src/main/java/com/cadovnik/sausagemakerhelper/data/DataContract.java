package com.cadovnik.sausagemakerhelper.data;

import android.provider.BaseColumns;

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

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key autoincrement") + ","
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

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key autoincrement") + ","
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

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key autoincrement") + ","
                    + String.format(TABLE_COLUMN, COLUMN_PROCESSES, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_DATE, "text", "") + ","
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
        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key autoincrement") + ","
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

    public static  final class SaltingHistoryDB implements BaseColumns{
        public final static String TABLE_NAME = "SaltingHistoryDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SALTINGS = "Saltings";

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key autoincrement") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SALTINGS, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }

    public static  final class SausageNoteDB implements BaseColumns{
        public final static String TABLE_NAME = "SausageNoteDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SAUSAGE_NAME = "SausageName";
        public final static String COLUMN_SALTING_UNIT = "SaltingUnitID";
        public final static String COLUMN_HEATING_PROCESS = "HeatingProcess";

        public final static String createTableString(){
            return String.format(CREATE_TABLE_FIRST_LINE, TABLE_NAME)
                    + String.format(TABLE_COLUMN, _ID, "integer", "primary key autoincrement") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SAUSAGE_NAME, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_SALTING_UNIT, "text", "") + ","
                    + String.format(TABLE_COLUMN, COLUMN_HEATING_PROCESS, "text", "")
                    + CREATE_TABLE_END_LINE;
        }
    }
}
