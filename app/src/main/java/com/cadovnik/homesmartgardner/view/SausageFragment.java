package com.cadovnik.homesmartgardner.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cadovnik.homesmartgardner.R;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SausageFragment extends Fragment {

    private final double sodium_ascorbate_value = 0.5;
    private final double phosphates_value = 3.0;
    private final double brine_percent = 10;
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
            TextView result = view.findViewById(R.id.salting_result);
            weight_of_meat = getValue(view, R.id.meat_weight_value, weight_of_meat);
            salting_percent = getValue(view, R.id.salting_percent_value, salting_percent);
            nitrite_salting_percent = getValue(view, R.id.nitrite_salt_percent_value, nitrite_salting_percent);
            String resultString = saltingCalculate();
            result.setText(resultString);
        });
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.sausage_maker);
        ((MainActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.salami);
    }

    private String saltingCalculate(){
        StringBuilder builder = new StringBuilder();



        return builder.toString();
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
}
