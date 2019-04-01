package com.cadovnik.homesmartgardner.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cadovnik.homesmartgardner.R;
import com.cadovnik.homesmartgardner.services.HeatingNotification;
import com.cadovnik.homesmartgardner.services.HeatingService;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressLint("ValidFragment")
public class HeatTreatmentFragment extends Fragment {
    private AlertDialog.Builder builder;
    private final String[] modes ={"Home", "SmokeHouse", "History"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.heat_treatment, container, false);
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choice Heating Mode");
        builder.setItems(modes, (dialog, item) -> {
            TextView text = getView().findViewById(R.id.currentMode);
            text.setText(modes[item]);
        });
        builder.setCancelable(false);

        TextView text = view.findViewById(R.id.currentMode);
        text.setOnClickListener(v -> builder.show());

        HeatingView outsideTemp = view.findViewById(R.id.outside_temp);
        outsideTemp.setOnClickListener(v -> HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test outside"));
        HeatingView probeTemp = view.findViewById(R.id.probe_temp);
        probeTemp.setOnClickListener(v -> HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test probe"));
        Button start = view.findViewById(R.id.start_button);
        start.setOnClickListener(v -> HeatingService.startBackgroundHeatingHandler(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.heat_treatment);
        ((MainActivity)getActivity()).getSupportActionBar().setIcon(R.mipmap.smoke_bomb);
    }
}
