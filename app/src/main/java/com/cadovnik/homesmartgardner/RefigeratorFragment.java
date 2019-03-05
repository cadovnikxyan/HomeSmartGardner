package com.cadovnik.homesmartgardner;

import android.app.DatePickerDialog;
import android.graphics.Color;
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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.formatter.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
//        xAxis.setTypeface(tfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new DateFormatter());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
//        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

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
    private class DateFormatter implements IAxisValueFormatter {

        private SimpleDateFormat mFormat;

        public DateFormatter() {
            mFormat = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value);
        }
    }

    private class HostoricalData extends AsyncTask<URL, Void, JSONArray> {
        protected JSONArray doInBackground(URL... urls) {
            String timestampFrom = "";
            String timestampTo = "";
            try{
                timestampFrom = String.valueOf(new SimpleDateFormat("MM/dd/yyyy").parse(dateFrom.getText().toString()).getTime());
                timestampTo = String.valueOf(new SimpleDateFormat("MM/dd/yyyy").parse(dateTo.getText().toString()));
            }
            catch (ParseException e) {
                Log.e("ParseException", "Error: " + e.toString());
            }
            return downloadRemoteTextFileContent(ServiceHistDataUrl  + "?fromtimestamp=" + timestampFrom + "&totimestamp=" + timestampTo);
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

    private void createLineGraph(JSONArray jArray) {
        lineChart.clear();
        try {
            LineData lineData = new LineData();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String sensName = jObject.getString("sensorName");
                String sensKind = jObject.getString("sensorKind");
                JSONArray vals = jObject.getJSONArray("values");

                List<Entry> entries = new ArrayList<Entry>();

                for (int j = 0; j < vals.length(); j++) {
                    JSONObject obj = vals.getJSONObject(j);
                    float x = (float) obj.getDouble("x");
                    float y = (float) obj.getDouble("y");
                    entries.add(new Entry(x, y));
                }
                LineDataSet dataSet = new LineDataSet(entries, sensKind);
                dataSet.setLabel(sensName);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setColor(ColorTemplate.getHoloBlue());
                dataSet.setValueTextColor(ColorTemplate.getHoloBlue());
                dataSet.setLineWidth(1.5f);
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setFillAlpha(65);
                dataSet.setFillColor(ColorTemplate.getHoloBlue());
                dataSet.setHighLightColor(Color.rgb(244, 117, 117));
                dataSet.setDrawCircleHole(false);
                lineData.addDataSet(dataSet);
                }
            lineChart.setData(lineData);
            lineChart.invalidate();
        } catch(JSONException e){
            Log.e("JSONException", "Error: " + e.toString());
        }
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
