package com.cadovnik.sausagemakerhelper.data;

import android.provider.BaseColumns;

public class HeatingContract {
    private HeatingContract(){

    }
    public static final class HeatingProcessUnit implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessUnit";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MODE = "mode";
        public final static String COLUMN_PROBE_TEMP = "probe_temp";
        public final static String COLUMN_TEMP = "temp";
        public final static String COLUMN_CONVECTION = "convection";
        public final static String COLUMN_HEATING = "heating";
        public final static String COLUMN_TIMESTAMP = "timestamp";

    }

    public static final class HeatingProcess implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcess";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_UNIT = "unit";
        public final static String COLUMN_START_DATE = "start_date";
        public final static String COLUMN_STOP_DATE = "stop_date";
        public final static String COLUMN_STATUS = "status";
    }

    public static final class HeatingProcessHistory implements BaseColumns {
        public final static String TABLE_NAME = "HeatingProcessHistory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PROCESSES = "heating_processes";
        public final static String COLUMN_DATE = "date";
    }
}
