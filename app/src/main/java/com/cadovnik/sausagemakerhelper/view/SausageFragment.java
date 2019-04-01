package com.cadovnik.sausagemakerhelper.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cadovnik.sausagemakerhelper.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class SausageFragment extends Fragment {

    private final double sodium_ascorbate_percent = 0.0005;
    private final double phosphates_percent = 0.003;
    private final double brine_percent = 0.1;
    private double weight_of_meat = 0;
    private double salting_percent = Double.valueOf(R.string.salting_percent_default);
    private double nitrite_salting_percent = Double.valueOf(R.string.nitrite_salt_default);
    private boolean wet_salting = false;
    private boolean with_phosphates = false;
    private boolean with_sodium_ascorbate = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.sausage_maker, container, false);
        Button calculate = view.findViewById(R.id.salting_calculate);
        calculate.setOnClickListener(v -> {
            RecyclerView result = view.findViewById(R.id.salting_result);
            weight_of_meat = getValue(view, R.id.meat_weight_value, weight_of_meat);
            salting_percent = getValue(view, R.id.salting_percent_value, salting_percent);
            nitrite_salting_percent = getValue(view, R.id.nitrite_salt_percent_value, nitrite_salting_percent);
            wet_salting = getValue(view, R.id.dry_wet);
            with_phosphates = getValue(view, R.id.phosphates);
            with_sodium_ascorbate = getValue(view, R.id.sodium_ascorbate);
            saltingCalculate(result);

        });
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.sausage_maker);
        ((MainActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.salami);
    }

    private void saltingCalculate(RecyclerView view){
        List<String> results = new ArrayList<String>();
        double rock_salt = 0.0;
        double nitrite_salt = 0.0;
        double phosphates = 0.0;
        double sodium_ascorbate = 0.0;
        double brine = 0.0;

        rock_salt = weight_of_meat * ( salting_percent / 100 );

        if ( wet_salting ){
            brine = weight_of_meat * brine_percent;
            rock_salt = (weight_of_meat + brine) * (salting_percent / 100);
            results.add(String.format("Brine weight: {}", brine));
        }
        if ( nitrite_salting_percent != 0 ) {
            nitrite_salt = rock_salt * (nitrite_salting_percent / 100);
            rock_salt = rock_salt - nitrite_salt;
            results.add(String.format("Nitrite salt weight: {}", nitrite_salt));
        }
        if ( with_phosphates ) {
            phosphates = weight_of_meat * phosphates_percent;
            results.add(String.format("Phosphates weight: {}", phosphates));
        }
        if ( with_sodium_ascorbate ) {
            sodium_ascorbate = weight_of_meat * sodium_ascorbate_percent;
            results.add(String.format("Sodium ascorbate weight: {}", sodium_ascorbate));
        }

        results.add(String.format("Rock salt weight: {}", rock_salt));

        for ( String str : results){
            TextView text = new TextView(getContext());
            text.setText(str);
            view.addView(text);
        }
    }

    private double getValue(View view, int id, double defaultValue){
        double result = 0.0;
        try{
            result = Double.valueOf(
                    ((TextInputEditText)view.findViewById(id)).getText().toString()
            );
        }catch (NumberFormatException e){
            result = defaultValue;
        }
        return result;
    }
    private boolean getValue(View view, int id){
        return ((CheckBox)view.findViewById(id)).isChecked();
    }
}
