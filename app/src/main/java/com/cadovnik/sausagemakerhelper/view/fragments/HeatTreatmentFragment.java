package com.cadovnik.sausagemakerhelper.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.http.HttpConnectionHandler;
import com.cadovnik.sausagemakerhelper.services.HeatingServiceProvider;
import com.cadovnik.sausagemakerhelper.services.IntentBuilder;
import com.cadovnik.sausagemakerhelper.services.ServiceCallback;
import com.cadovnik.sausagemakerhelper.view.HeatingView;
import com.cadovnik.sausagemakerhelper.view.MainActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@IntDef({MODE.MANUAL, MODE.AUTO, MODE.SMOKING})
@Retention(RetentionPolicy.SOURCE)
@interface MODE {

    int MANUAL = 0x1000000;
    int AUTO = 0x2000000;
    int SMOKING = 0x3000000;
}

@IntDef({HEATING_STATE.NONE,HEATING_STATE.FRYING, HEATING_STATE.DRYING, HEATING_STATE.BOILING})
@Retention(RetentionPolicy.SOURCE)
@interface HEATING_STATE{
    int NONE = 0x0010000;
    int DRYING = 0x0020000;
    int FRYING = 0x0030000;
    int BOILING = 0x0040000;
}

@SuppressLint("ValidFragment")
public class HeatTreatmentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnDragListener{
    private final Map<String, Integer> modes = new HashMap();
    private final Map<Integer, String> heatingStates = new HashMap();
    private List<String> modesArray = new ArrayList<>();
    private ArrayAdapter<String>  heatTreatmentModeAdapter;
    private static HeatTreatmentFragment instance = null;
    private JSONObject currentState = new JSONObject();
    private boolean fromJSONState = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LineChart lineChart;
    private Timer currentStateTimer;
    private HeatingServiceProvider provider = null;
    private Callback espGetCurrentStateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(this.getClass().toString(), "Error: " + e.toString());
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                JSONObject jobj = new JSONObject(response.body().string());
                Activity activity = getActivity();
                if ( activity == null )
                    return;
                activity.runOnUiThread(() -> {
                    fromJSONState = true;
                    setCurrentState(jobj);
                    fromJSONState = false;
                    Log.e(this.getClass().toString(), "JSON ESP: " + jobj.toString());
                });
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }
            swipeRefreshLayout.setRefreshing(false);

        }
    };

    private Callback espSetCurrentStateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(this.getClass().toString(), "Error: " + e.toString());
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                JSONObject jobj = new JSONObject(response.body().string());
                getActivity().runOnUiThread(() -> {
                    fromJSONState = true;
                    setCurrentState(jobj);
                    fromJSONState = false;
                });
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }
        }
    };

    public static HeatTreatmentFragment newInstance(){
        if ( instance == null )
            instance = new HeatTreatmentFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modes.clear();
        modes.put(getResources().getString(R.string.heating_heat_mode_manual), MODE.MANUAL);
        modes.put(getResources().getString(R.string.heating_heat_mode_auto), MODE.AUTO);
        modes.put(getResources().getString(R.string.heating_heat_mode_smoking), MODE.SMOKING);

        heatingStates.clear();
        heatingStates.put(HEATING_STATE.NONE, getResources().getString(R.string.heating_heat_state_none));
        heatingStates.put(HEATING_STATE.DRYING, getResources().getString(R.string.heating_heat_state_drying));
        heatingStates.put(HEATING_STATE.FRYING, getResources().getString(R.string.heating_heat_state_frying));
        heatingStates.put(HEATING_STATE.BOILING, getResources().getString(R.string.heating_heat_state_boiling));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.heat_treatment, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        lineChart = view.findViewById(R.id.chartSmoking);

        view.findViewById(R.id.start).setOnClickListener(v -> startHeatingProcess());
        ((Switch)view.findViewById(R.id.convectionOnOff)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                currentState.remove("convectionState");
                currentState.put("convectionState", isChecked);
                sendCurrentState();
            } catch (JSONException e) {
                Log.e(HeatTreatmentFragment.this.getClass().toString(), "JSON ESP: ", e);
            }
        });
        ((Switch)view.findViewById(R.id.smoking_air)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                currentState.remove("airPumpState");
                currentState.put("airPumpState", isChecked);
                sendCurrentState();
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }
        });
        ((Switch)view.findViewById(R.id.water)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                currentState.remove("waterPumpState");
                currentState.put("waterPumpState", isChecked);
                sendCurrentState();
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }
        });
        ((Switch)view.findViewById(R.id.ignition)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                currentState.remove("ignitionState");
                currentState.put("ignitionState", isChecked);
                sendCurrentState();
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }
        });

        if ( !HttpConnectionHandler.getInstance().IsFindedESP()){
            setSwitchEnabled(false);
        }
        Timer timerDNS = new Timer();
        timerDNS.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               checkConnection(timerDNS);
            }
        }, 0, 300);
        currentStateTimer = new Timer();
        Spinner currentMode = view.findViewById(R.id.currentMode);
        getCurrentState();
        for ( String mode : modes.keySet() )
            modesArray.add(mode);

        Collections.reverse(modesArray);
        heatTreatmentModeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, modesArray);
        heatTreatmentModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentMode.setAdapter(heatTreatmentModeAdapter);
        currentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currentString = modesArray.get(position);
                Integer modValue = modes.get(currentString);
                try {
                    currentState.put("mode", modValue);
                    if (!modValue.equals(MODE.MANUAL)){
                        getView().findViewById(R.id.convectionOnOff).setEnabled(false);
                        getView().findViewById(R.id.smoking_air).setEnabled(false);
                        getView().findViewById(R.id.water).setEnabled(false);
                    }else{
                        setSwitchEnabled(true);
                    }

                    sendCurrentState();
                } catch (JSONException e) {
                    Log.e(this.getClass().toString(), "JSON ESP: " , e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
    private void setSwitchEnabled(boolean state){
        getView().findViewById(R.id.convectionOnOff).setEnabled(state);
        getView().findViewById(R.id.smoking_air).setEnabled(state);
        getView().findViewById(R.id.water).setEnabled(state);
        getView().findViewById(R.id.ignition).setEnabled(state);
        getView().findViewById(R.id.start).setEnabled(state);
    }

    private void setCurrentState(JSONObject object){
        try{
            currentState = object;
            Spinner currentMode = getView().findViewById(R.id.currentMode);
            Integer modeInt = modes.get(currentState.getInt("mode"));
            for (int i = 0; i < modesArray.size(); ++i ) {
                if ( modes.get(modesArray.get(i)).equals(modeInt))
                    currentMode.setSelection(i);
            }
            ((Switch)getView().findViewById(R.id.convectionOnOff)).setChecked(currentState.getBoolean("convectionState"));
            ((Switch)getView().findViewById(R.id.smoking_air)).setChecked(currentState.getBoolean("airPumpState"));
            ((Switch)getView().findViewById(R.id.water)).setChecked(currentState.getBoolean("waterPumpState"));
            ((Switch)getView().findViewById(R.id.ignition)).setChecked(currentState.getBoolean("ignitionState"));
            ((HeatingView)getView().findViewById(R.id.probe_temp)).addTextOnImage(String.format("%.2f", currentState.getDouble("currentProbeTemp")) + " \u2103");
            ((HeatingView)getView().findViewById(R.id.outside_temp)).addTextOnImage(String.format("%.2f", currentState.getDouble("currentOutTemp")) + " \u2103");
            ((TextView)getView().findViewById(R.id.currentHeatStatus)).setText(heatingStates.get(currentState.getInt("heatingMode")));
            ((TextView)getView().findViewById(R.id.start)).setText(currentState.getBoolean("started") ? R.string.stop_heating : R.string.start_heating);
            if ( currentState.getBoolean("started") ){
                addDataToChart();
            }
        }catch (JSONException e){
            Log.e(this.getClass().toString(), "JSON ESP: " , e);
        }catch (NullPointerException e ){
            Log.e(this.getClass().toString(), "NullPointer ESP: " , e);
        }
    }

    private void checkConnection(Timer timer){
        try{
            if ( !HttpConnectionHandler.getInstance().IsFindedESP()){
                getActivity().runOnUiThread(() -> setSwitchEnabled(false));
            }
            else{
                getActivity().runOnUiThread(() -> {
                    setSwitchEnabled(true);
                    timer.cancel();
                });
            }
        }catch (NullPointerException e){
            Log.e(this.getClass().toString(), "Timer: " , e);
        }
    }
    private void getCurrentState(){
        try{
            if ( HttpConnectionHandler.getInstance().IsFindedESP()){
                HttpConnectionHandler.getInstance().getESPRequest("GetCurrentState", espGetCurrentStateCallback);
            }
            else{
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Connection to ESP isn`t exists", Toast.LENGTH_SHORT).show();
                });
            }
        }catch (NullPointerException e){
            Log.e(this.getClass().toString(), "Timer: " , e);
        }
    }

    private void startHeatingProcess(){
        try {
            if ( !currentState.has("started") ){
                currentState.put("started", false);
            }
            if ( provider == null )
                provider = new HeatingServiceProvider((MainActivity) getActivity());

            if (  !currentState.getBoolean("started") ){
                ((TextView)getView().findViewById(R.id.start)).setText(R.string.stop_heating);
                currentState.remove("started");
                currentState.put("started", true);
                currentStateTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        getCurrentState();
                    }
                }, 0, 10000);

                setUpChart();
                Intent service = new IntentBuilder(getActivity().getApplicationContext()).setCommand(1).setJson(currentState).build();

                provider.serviceBind(service);
                provider.serviceDataCallback(new ServiceCallback<JSONObject>() {
                    @Override
                    public void ReceiveData(JSONObject o) {
                        fromJSONState = true;
                        setCurrentState(o);
                        fromJSONState = false;
                    }
                });
                getActivity().getApplicationContext().startService(service);
            }else{
                ((TextView)getView().findViewById(R.id.start)).setText(R.string.start_heating);
                currentState.remove("started");
                currentState.put("started", false);
                provider.serviceUnbind();

            }
            sendCurrentState();


        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "JSON ESP: " , e);
        }
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

    private void addDataToChart() throws JSONException {
        double currentProbe = currentState.getDouble("currentProbeTemp");
        double currentOutTemp = currentState.getDouble("currentOutTemp");
        LineData lineData = new LineData();

        LineDataSet dataSetProbe = addDataToSet("currentProbeTemp", currentProbe);
        LineDataSet dataSetOutTemp = addDataToSet("currentOutTemp", currentOutTemp);

        lineData.addDataSet(dataSetProbe);
        lineData.addDataSet(dataSetOutTemp);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private LineDataSet addDataToSet(String name, double value){
        List<Entry> entries = new ArrayList<Entry>();
        LineDataSet dataSet = new LineDataSet(entries, name);
        entries.add(new Entry((float)Calendar.getInstance().getTimeInMillis(), (float)value));
        dataSet.setLabel(name);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setFillAlpha(65);
        if ( name.equals("currentOutTemp")){
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

        return dataSet;
    }
    private void sendCurrentState(){
        if (!fromJSONState)
            HttpConnectionHandler.getInstance().postESPRequest("SetState", currentState.toString(), espSetCurrentStateCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if ( currentState.getBoolean("started")){
//              HeatingService.startBackgroundHeatingHandler(getActivity());
            }
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "JSON ESP: " , e);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.heat_treatment);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        HttpConnectionHandler.getInstance().getESPRequest("GetCurrentState", espGetCurrentStateCallback);
    }

    private class DateFormatter implements IAxisValueFormatter {

        private SimpleDateFormat mFormat;

        public DateFormatter() {
            mFormat = new SimpleDateFormat("HH:mm:ss");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String date = mFormat.format(value);
            return date;
        }
    }
}
