package com.cadovnik.sausagemakerhelper.view.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.http.HttpConnectionHandler;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RefigeratorFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnDragListener {
    private LineChart lineChart;
    private TableLayout lastedTableLayout = null;
    private TextView dateFrom = null;
    private TextView dateTo = null;
    private Callback graphCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(e.getClass().toString(), "Error: " + e.toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            JSONArray jarray = null;
            try {
                jarray = new JSONArray(response.body().string());
            } catch (JSONException e) {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
                catch (NullPointerException ee){

                }
                return;
            }
            JSONArray finalJarray = jarray;
            try{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createLineGraph(finalJarray);
                    }
                });
            }catch (NullPointerException e) {

            }
        }
    };
    private Callback lastedDataCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            JSONArray jarray = null;
            try {
                jarray = new JSONArray(response.body().string());
            } catch (JSONException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            JSONArray finalJarray = jarray;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createLatesValueTable(finalJarray);
                }
            });
        }
    };

    Calendar dateCFrom =  null;
    Calendar dateCTo =  null;
    private static final String ServiceHistDataUrl = "https://cadovnik.fvds.ru:9999/sensors/historical-sensordata";
    private  static  final String ServiceLatestDataUrl =  "https://cadovnik.fvds.ru:9999/sensors/latest-sensordata/";

    private static final String DateFormat = "MM/dd/yyyy";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static RefigeratorFragment instance = null;
    public static RefigeratorFragment newInstance(){
        if ( instance == null )
            instance = new RefigeratorFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.refrigerate_chart, container, false);
        HttpConnectionHandler.Initialize(getActivity().getResources().openRawResource(R.raw.certificate));
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        lastedTableLayout = view.findViewById(R.id.lasted_data_table);
        lineChart = view.findViewById(R.id.chart);
        dateFrom = view.findViewById(R.id.dateFrom);
        dateTo = view.findViewById(R.id.dateTo);
        setUpChart();
        setUpDates();
        FloatingActionsMenu menu = view.findViewById(R.id.multiple_actions);
        FloatingActionButton loadHistory = view.findViewById(R.id.load_file_from_server);
        loadHistory.setOnClickListener(view1 -> {
            HttpConnectionHandler.getInstance().getRequest(getTimeStampResponce(), graphCallback );
            mSwipeRefreshLayout.setRefreshing(true);
            menu.collapse();
        });

        FloatingActionButton loadLasted = view.findViewById(R.id.get_lasted);
        loadLasted.setOnClickListener(v -> {
            HttpConnectionHandler.getInstance().getRequest(ServiceLatestDataUrl, lastedDataCallback);
            mSwipeRefreshLayout.setRefreshing(true);
            menu.collapse();
        });
        return view;
    }

    private String getTimeStampResponce(){
        String timestampFrom = "";
        String timestampTo = "";
        try{
            timestampFrom = String.valueOf(new SimpleDateFormat("MM/dd/yyyy").parse(dateFrom.getText().toString()).getTime());
            timestampTo = String.valueOf(new SimpleDateFormat("MM/dd/yyyy").parse(dateTo.getText().toString()));
        }
        catch (ParseException e) {
            Log.e("ParseException", "Error: " + e.toString());
        }
        return ServiceHistDataUrl  + "?fromtimestamp=" + timestampFrom + "&totimestamp=" + timestampTo;
    }


    private void setUpChart(){

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setBackgroundColor(Color.parseColor("#f0f8ff"));
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        lineChart.animateX(1000);
        lineChart.getDescription().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.parseColor("#943a05"));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(0.2f);
        xAxis.setValueFormatter(new DateFormatter());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextSize(15f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.parseColor("#943a05"));

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    private void setUpDates(){
        dateCFrom = Calendar.getInstance();
        dateCTo = Calendar.getInstance();
        final String currentDate = new SimpleDateFormat(DateFormat).format(Calendar.getInstance().getTime());
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
        dateFrom.setText(currentDate);
        dateTo.setText(currentDate);
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.refrigerator);
//        ((MainActivity)getActivity()).getSupportActionBar().setIcon(R.mipmap.refrigerator);
        StartRefresh();
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
        dateFrom.setText(new SimpleDateFormat(DateFormat).format(dateCFrom.getTimeInMillis()).toString());
    }

    public void setToDate(){
        dateTo.setText(new SimpleDateFormat(DateFormat).format(dateCTo.getTimeInMillis()).toString());
    }

    @Override
    public void onRefresh() {
        StartRefresh();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }

    private void StartRefresh(){
        HttpConnectionHandler.getInstance().getRequest(getTimeStampResponce(), graphCallback);
        HttpConnectionHandler.getInstance().getRequest(ServiceLatestDataUrl, lastedDataCallback);
        mSwipeRefreshLayout.setRefreshing(true);
    }
    private class DateFormatter implements IAxisValueFormatter {

        private SimpleDateFormat mFormat;

        public DateFormatter() {
            mFormat = new SimpleDateFormat("dd MMM HH:mm");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String date = mFormat.format(value);
            return date;
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
                LineDataSet dataSet = new LineDataSet(entries, sensName);
                dataSet.setLabel(sensKind);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setLineWidth(3f);
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setFillAlpha(65);
                if ( sensKind.equals("temperature")){
                    dataSet.setValueTextColor(Color.parseColor("#f13934"));
                    dataSet.setColor(Color.parseColor("#f13934"));
                    dataSet.setFillColor(Color.parseColor("#f13934"));
                    dataSet.setHighLightColor(Color.parseColor("#f13934"));
                }else{
                    dataSet.setValueTextColor(Color.parseColor("#001cb2"));
                    dataSet.setColor(Color.parseColor("#001cb2"));
                    dataSet.setFillColor(Color.parseColor("#001cb2"));
                    dataSet.setHighLightColor(Color.parseColor("#001cb2"));
                }
                dataSet.setDrawCircleHole(false);
                lineData.addDataSet(dataSet);
                }
            lineChart.setData(lineData);
            lineChart.invalidate();
        } catch(JSONException e){
            Log.e("JSONException", "Error: " + e.toString());
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @SuppressLint("ResourceType")
    private void createLatesValueTable(JSONArray jsonArray){
        if(jsonArray != null){
            int childCount = lastedTableLayout.getChildCount();
            if ( childCount > 1 ) {
                lastedTableLayout.removeViews(1 , childCount - 1);
            }
            ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            for (int i = 0; i < jsonArray.length(); i++) {
                try{
                    TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.refrigerator_table_row, null);
                    tableRow.setId(R.integer.tableRowId1);
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    String sensName = jObject.getString("sensorName");
                    String sensKind = jObject.getString("sensorKind");
                    JSONArray vals = jObject.getJSONArray("values");

                    TextView viewSName = tableRow.findViewById(R.id.sensName1);
                    viewSName.setText(sensName);

                    TextView viewSKind = tableRow.findViewById(R.id.sensKind1);
                    viewSKind.setText(sensKind);

                    TextView viewSValDate = tableRow.findViewById(R.id.valTime1);
                    viewSValDate.setText(new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date(vals.getJSONObject(0).getLong("x"))));

                    TextView viewSVal = tableRow.findViewById(R.id.val1);
                    viewSVal.setText(vals.getJSONObject(0).getString("y"));

                    lastedTableLayout.addView(tableRow, i + 1);
                }
                catch (JSONException e) {
                    Log.e("JSONException", "Error: " + e.toString());
                }
                catch (NullPointerException e){
                    Log.e(e.getClass().toString(), "Error: " + e.toString());
                }
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
