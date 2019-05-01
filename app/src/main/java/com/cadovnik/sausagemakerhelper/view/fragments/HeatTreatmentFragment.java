package com.cadovnik.sausagemakerhelper.view.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.http.HttpConnectionHandler;
import com.cadovnik.sausagemakerhelper.services.HeatingNotification;
import com.cadovnik.sausagemakerhelper.view.HeatingView;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressLint("ValidFragment")
public class HeatTreatmentFragment extends Fragment {
    private AlertDialog.Builder builder;
    private final String[] modes ={"Manual", "Auto", "Smoking", "No Heating"};
    private static HeatTreatmentFragment instance = null;
    private JSONObject currentState = new JSONObject();
    private Callback espGetCurrentStateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(this.getClass().toString(), "Error: " + e.toString());
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT);
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                JSONObject jobj = new JSONObject(response.body().string());
                getActivity().runOnUiThread(() -> {
                    setCurrentState(jobj);
                    Log.e(this.getClass().toString(), "JSON ESP: " + jobj.toString());
                });
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }

        }
    };

    private Callback espSetCurrentStateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(this.getClass().toString(), "Error: " + e.toString());
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT);
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String res = response.body().string();
            getActivity().runOnUiThread(() -> {
                    Log.e(this.getClass().toString(), res);
                    Toast.makeText(getContext(), res, Toast.LENGTH_SHORT);
            });
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

        TextView text = view.findViewById(R.id.currentMode);
        text.setOnClickListener(v -> builder.show());

        HeatingView outsideTemp = view.findViewById(R.id.outside_temp);
        outsideTemp.setOnClickListener(v -> HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test outside"));
        HeatingView probeTemp = view.findViewById(R.id.probe_temp);
        probeTemp.setOnClickListener(v -> HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test probe"));

        ((Switch)view.findViewById(R.id.convectionOnOff)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                currentState.remove("convectionState");
                currentState.put("convectionState", isChecked);
                HeatTreatmentFragment.this.sendCurrentState();
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
        FloatingActionButton getCurrentState = view.findViewById(R.id.get_current_state);
        getCurrentState.setOnClickListener(v -> HttpConnectionHandler.getInstance().getESPRequest("GetCurrentState", espGetCurrentStateCallback));
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
        }catch (JSONException e){
            Log.e(this.getClass().toString(), "JSON ESP: " , e);
        }
    }

    private void sendCurrentState(){
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
}
