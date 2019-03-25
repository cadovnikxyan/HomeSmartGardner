package com.cadovnik.homesmartgardner.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cadovnik.homesmartgardner.services.HeatingNotification;
import com.cadovnik.homesmartgardner.R;
import com.cadovnik.homesmartgardner.services.HeatingService;

@SuppressLint("ValidFragment")
public class HeatTreatmentFragment extends Fragment {
    private AlertDialog.Builder builder;
    final String[] modes ={"Home", "SmokeHouse", "History"};
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
        builder.setItems(modes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                TextView text = (TextView) getView().findViewById(R.id.currentMode);
                text.setText(modes[item]);
            }
        });
        builder.setCancelable(false);

        TextView text = (TextView) view.findViewById(R.id.currentMode);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
        Button outsideTemp = (Button) view.findViewById(R.id.outside_temp);
        outsideTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test outside");
            }
        });
        Button probeTemp = (Button) view.findViewById(R.id.probe_temp);
        probeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeatingNotification.sendNotify(getActivity().getApplicationContext(),"Test probe");
            }
        });
        Button start = (Button) view.findViewById(R.id.start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeatingService.startBackgroundHeatingHandler(getActivity());
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.heat_treatment);
        ((MainActivity)getActivity()).getSupportActionBar().setIcon(R.mipmap.smoke_bomb);
    }
}
