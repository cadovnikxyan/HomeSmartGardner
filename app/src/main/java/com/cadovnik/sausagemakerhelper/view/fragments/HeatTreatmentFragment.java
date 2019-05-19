package com.cadovnik.sausagemakerhelper.view.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.http.HttpConnectionHandler;
import com.cadovnik.sausagemakerhelper.services.HeatingNotification;
import com.cadovnik.sausagemakerhelper.view.HeatingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressLint("ValidFragment")
public class HeatTreatmentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnDragListener{
    private AlertDialog.Builder builder;
    private final String[] modes = {"Manual", "Auto", "Smoking", "No Heating"};
    private static HeatTreatmentFragment instance = null;
    private JSONObject currentState = new JSONObject();
    private boolean fromJSONState = false;
    private SwipeRefreshLayout swipeRefreshLayout;
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
                getActivity().runOnUiThread(() -> {
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
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choice Heating Mode");
        builder.setItems(modes, (dialog, item) -> {
            TextView text = getView().findViewById(R.id.currentMode);
            text.setText(modes[item]);
        });
        builder.setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.heat_treatment, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        HeatingView outsideTemp = view.findViewById(R.id.outside_temp);
        outsideTemp.setOnClickListener(v -> HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test outside"));
        HeatingView probeTemp = view.findViewById(R.id.probe_temp);
        probeTemp.setOnClickListener(v -> HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test probe"));

        ((Switch)view.findViewById(R.id.convectionOnOff)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                currentState.remove("convectionState");
                currentState.put("convectionState", isChecked);
                sendCurrentState();
            } catch (JSONException e) {
                Log.e(HeatTreatmentFragment.this.getClass().toString(), "JSON ESP: ", e);
            }
        });
        ((Switch)view.findViewById(R.id.heating)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                currentState.remove("heatingState");
                currentState.put("heatingState", isChecked);
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
            view.findViewById(R.id.convectionOnOff).setEnabled(false);
            view.findViewById(R.id.heating).setEnabled(false);
            view.findViewById(R.id.water).setEnabled(false);
        }
        Timer timerDNS = new Timer();
        timerDNS.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try{
                    if ( !HttpConnectionHandler.getInstance().IsFindedESP()){
                        getActivity().runOnUiThread(() -> {
                            getView().findViewById(R.id.convectionOnOff).setEnabled(false);
                            getView().findViewById(R.id.heating).setEnabled(false);
                            getView().findViewById(R.id.water).setEnabled(false);
                            getView().findViewById(R.id.ignition).setEnabled(false);
                        });
                    }
                    else{
                        getActivity().runOnUiThread(() -> {
                            getView().findViewById(R.id.convectionOnOff).setEnabled(true);
                            getView().findViewById(R.id.heating).setEnabled(true);
                            getView().findViewById(R.id.water).setEnabled(true);
                            getView().findViewById(R.id.ignition).setEnabled(true);
                            this.cancel();
                        });
                    }
                }catch (NullPointerException e){
                    Log.e(this.getClass().toString(), "Timer: " , e);
                }

            }
        }, 0, 300);
        Timer currentStateTimer = new Timer();
        currentStateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
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
        }, 0, 10000);

        Spinner currentMode = view.findViewById(R.id.currentMode);
        ArrayList<String> modesArray = new ArrayList<>();
        for ( String mode : modes )
            modesArray.add(mode);
        HeatTreatmentModeAdapter heatTreatmentModeAdapter = new HeatTreatmentModeAdapter(getContext(), 0, modesArray);
        currentMode.setAdapter(heatTreatmentModeAdapter);

        return view;
    }

    private void setCurrentState(JSONObject object){
        try{
            currentState = object;
            TextView text = getView().findViewById(R.id.currentMode);
            text.setText(currentState.getString("mode"));
            ((Switch)getView().findViewById(R.id.convectionOnOff)).setChecked(currentState.getBoolean("convectionState"));
            ((Switch)getView().findViewById(R.id.heating)).setChecked(currentState.getBoolean("heatingState"));
            ((Switch)getView().findViewById(R.id.water)).setChecked(currentState.getBoolean("waterPumpState"));
            ((Switch)getView().findViewById(R.id.ignition)).setChecked(currentState.getBoolean("ignitionState"));
            ((HeatingView)getView().findViewById(R.id.probe_temp)).addTextOnImage(currentState.getString("currentProbeTemp"));
            ((HeatingView)getView().findViewById(R.id.outside_temp)).addTextOnImage(currentState.getString("currentOutTemp"));
            ((TextView)getView().findViewById(R.id.currentHeatStatus)).setText(currentState.getString("heatingMode"));
        }catch (JSONException e){
            Log.e(this.getClass().toString(), "JSON ESP: " , e);
        }
    }

    private void sendCurrentState(){
        if (!fromJSONState)
            HttpConnectionHandler.getInstance().postESPRequest("SetState", currentState.toString(), espSetCurrentStateCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        HeatingService.startBackgroundHeatingHandler(getActivity());
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

    public static class HeatTreatmentModeAdapter extends ArrayAdapter<ArrayList> {

        public HeatTreatmentModeAdapter(Context context, int resource, ArrayList objects) {
            super(context, resource, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;

        }

        private class ViewHolder {
            private TextView text;
        }
    }
}
