package com.cadovnik.homesmartgardner;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RefigeratorFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private LineChart lineChart;
    private TableLayout lastedTableLayout = null;
    private TextView dateFrom = null;
    private TextView dateTo = null;
    Calendar dateCFrom =  null;
    Calendar dateCTo =  null;
    private static final String ServiceHistDataUrl = "http://www.cadovnik.fvds.ru:9999/sensors/historical-sensordata";
    private  static  final String ServiceLatestDataUrl =  "http://cadovnik.fvds.ru:9999/sensors/latest-sensordata/";
    public static MainFragment newInstance(int index) {
        MainFragment f = new MainFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist. The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // isn't displayed. Note this isn't needed -- we could just
            // run the code below, where we would create and return the
            // view hierarchy; it would just never be used.
            return null;
        }
        View view = inflater.inflate(R.layout.refrigerate_chart, container, false);
        lastedTableLayout = (TableLayout) view.findViewById(R.id.lasted_data_table);
        lineChart = (LineChart) view.findViewById(R.id.chart);
        dateFrom = (TextView) view.findViewById(R.id.dateFrom);
        dateCFrom = Calendar.getInstance();
        dateCTo = Calendar.getInstance();
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), dFrom,
                        dateCFrom.get(Calendar.YEAR),
                        dateCFrom.get(Calendar.MONTH),
                        dateCFrom.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        dateTo = (TextView) view.findViewById(R.id.dateTo);
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), dTo,
                        dateCTo.get(Calendar.YEAR),
                        dateCTo.get(Calendar.MONTH),
                        dateCTo.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
        Button loadTextButton = (Button)view.findViewById(R.id.load_file_from_server);
        loadTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HostoricalData().execute();
            }
        });

        Button loadLasted = (Button)view.findViewById(R.id.get_lasted);
        loadLasted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LastedData().execute();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Refigerator");
    }

    DatePickerDialog.OnDateSetListener dFrom = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCFrom.set(Calendar.YEAR, year);
            dateCFrom.set(Calendar.MONTH, monthOfYear);
            dateCFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setFromDate();
        }
    };
    DatePickerDialog.OnDateSetListener dTo = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCTo.set(Calendar.YEAR, year);
            dateCTo.set(Calendar.MONTH, monthOfYear);
            dateCTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setToDate();
        }
    };
    public void setFromDate(){
        dateFrom.setText(new SimpleDateFormat("MM/dd/yyyy").format(dateCFrom.getTimeInMillis()).toString());
    }

    public void setToDate(){
        dateTo.setText(new SimpleDateFormat("MM/dd/yyyy").format(dateCTo.getTimeInMillis()).toString());
    }
    private class DateFormatter implements IValueFormatter {

        private SimpleDateFormat mFormat;

        public DateFormatter() {
            mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm ");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + " $"; // e.g. append a dollar-sign
        }
    }

    private class HostoricalData extends AsyncTask<URL, Void, JSONArray> {
        protected JSONArray doInBackground(URL... urls) {
            return downloadRemoteTextFileContent(ServiceHistDataUrl  + "?fromtimestamp=" + dateFrom.getText() + "&totimestamp=" + dateTo.getText());
        }
        protected void onPostExecute(JSONArray result) {
            if(result != null){
                createLineGraph(result);
            }
        }
    }

    private  class LastedData extends  AsyncTask<URL, Void, JSONArray>{
        @Override
        protected JSONArray doInBackground(URL... urls) {
            return downloadRemoteTextFileContent(ServiceLatestDataUrl);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray != null){
                for (int i = 0; i < jsonArray.length(); i++) {
                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT));
                    try{
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String sensName = jObject.getString("sensorName");
                        String sensKind = jObject.getString("sensorKind");
                        JSONArray vals = jObject.getJSONArray("values");
                        TextView viewSName = new TextView(getContext());
                        viewSName.setText(sensName);
                        TextView viewSKind = new TextView(getContext());
                        viewSKind.setText(sensKind);
                        TextView viewSValDate = new TextView(getContext());
                        viewSValDate.setText(new SimpleDateFormat("yy/MM/dd hh:mm:ss ").format(vals.getJSONObject(0).getLong("x")));
                        TextView viewSVal = new TextView(getContext());
                        viewSVal.setText(vals.getJSONObject(0).getString("y"));
                        tableRow.addView(viewSName);
                        tableRow.addView(viewSKind);
                        tableRow.addView(viewSValDate);
                        tableRow.addView(viewSVal);
                        lastedTableLayout.addView(tableRow, i + 1);
                    }
                    catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                    }
                }
            }
        }
    }

    private void createLineGraph(JSONArray jArray){
        for (int i = 0; i < jArray.length(); i++) {

            try
            {
                JSONObject jObject = jArray.getJSONObject(i);
                String sensName = jObject.getString("sensorName");
                String sensKind = jObject.getString("sensorKind");
                JSONArray vals = jObject.getJSONArray("values");

                List<Entry> entries = new ArrayList<Entry>();

                for (int j = 0; i < vals.length(); i++){
                    JSONObject obj = vals.getJSONObject(j);
                    entries.add(new Entry((float)obj.getDouble("x"), (float)obj.getDouble("y")));
                }
                LineDataSet dataSet = new LineDataSet(entries, sensKind);
                dataSet.setValueFormatter(new DateFormatter());
                dataSet.setLabel(sensName);
                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }


    } // End Loop

    }

    private JSONArray downloadRemoteTextFileContent(String reqURL){
        URL mUrl = null;
        JSONArray jArray = null;
        try {
            mUrl = new URL(reqURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        };
        try {
            URLConnection connection = mUrl.openConnection();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line);
            }
            bReader.close();
            String result = sBuilder.toString();
            jArray = new JSONArray(result);

        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        } catch (Exception e) {
            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
        }
        return jArray;
    }
}
