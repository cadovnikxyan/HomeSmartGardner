package com.cadovnik.sausagemakerhelper.data;

import android.provider.BaseColumns;

public class HeatingContract {
    private HeatingContract(){

    }
    public static final class HeatingProcessUnitDB implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessUnitDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MODE = "mode";
        public final static String COLUMN_PROBE_TEMP = "probe_temp";
        public final static String COLUMN_TEMP = "temp";
        public final static String COLUMN_CONVECTION = "convection";
        public final static String COLUMN_HEATING = "heating";
        public final static String COLUMN_TIMESTAMP = "timestamp";

    }

    public static final class HeatingProcessDB implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_UNIT = "unit";
        public final static String COLUMN_START_DATE = "start_date";
        public final static String COLUMN_STOP_DATE = "stop_date";
        public final static String COLUMN_STATUS = "status";
    }

    public static final class HeatingProcessHistoryDB implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessHistoryDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PROCESSES = "heating_processes";
        public final static String COLUMN_DATE = "date";
    }

    public static  final class SaltingUnitDB implements  BaseColumns{
        public final static String TABLE_NAME = "SaltingUnitDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SAUSAGE_NAME = "SausageName";
        public final static String COLUMN_MEAT_WEIGHT = "MeatWeight";
        public final static String COLUMN_ROCK_SALT_WEIGHT = "RockSaltWeight";
        public final static String COLUMN_NITRITE_SALT_WEIGHT = "NitriteSaltWeight";
        public final static String COLUMN_PHOSPHATES_WEIGHT = "PhosphatesWeight";
        public final static String COLUMN_SODIUM_ASCORBATE_WEIGHT = "SodiumAscorbateWeight";
        public final static String COLUMN_BRINE_WEIGHT = "BrineWeight";
        public final static String COLUMN_DATE = "date";
    }

    public static  final class SaltingHistoryDB implements  BaseColumns{
        public final static String TABLE_NAME = "SaltingHistoryDB";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SALTINGS = "saltings";
    }
}
