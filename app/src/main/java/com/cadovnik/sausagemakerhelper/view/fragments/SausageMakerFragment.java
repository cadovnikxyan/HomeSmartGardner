package com.cadovnik.sausagemakerhelper.view.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.data.SaltingUnit;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class SausageMakerFragment extends Fragment {

    private SaltingUnit saltingUnit;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public static class InputFilterMinMax implements InputFilter {

        private double min, max;

        public InputFilterMinMax(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Double.parseDouble(min);
            this.max = Double.parseDouble(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                double input = Double.parseDouble(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(double a, double b, double c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.sausage_maker, container, false);
        FloatingActionsMenu menu = view.findViewById(R.id.multiple_actions);
        FloatingActionButton calculate = view.findViewById(R.id.salting_calculate);
        TextInputEditText salting = view.findViewById(R.id.salting_percent_value);
        salting.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        TextInputEditText nitrite_salting = view.findViewById(R.id.nitrite_salt_percent_value);
        nitrite_salting.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        calculate.setOnClickListener(v -> {
            saltingUnit = new SaltingUnit();
            saltingUnit.setWet_salting(getValue(view, R.id.dry_wet));
            saltingUnit.setNitrite_salting_percent(getValue(view, R.id.nitrite_salt_percent_value, Double.valueOf(R.string.nitrite_salt_default)));
            saltingUnit.setSalting_percent(getValue(view, R.id.salting_percent_value, Double.valueOf(R.string.salting_percent_default)));
            saltingUnit.setWeight_of_meat(getValue(view, R.id.meat_weight_value, 0));
            saltingUnit.setWith_phosphates(getValue(view, R.id.phosphates));
            saltingUnit.setWith_sodium_ascorbate(getValue(view, R.id.sodium_ascorbate));

            RecyclerView result = view.findViewById(R.id.salting_result);
            result.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getActivity());
            result.setLayoutManager(layoutManager);

            saltingCalculate(result);
            menu.collapse();

        });
        FloatingActionButton save = view.findViewById(R.id.salting_save);
        save.setOnClickListener( v -> {
            saltingUnit.convert();
            menu.collapse();
        });
        return view;
    }

    public static class SaltingAdapter extends RecyclerView.Adapter<SaltingAdapter.ViewHolder>{
        private List<String> data;
        public SaltingAdapter(List<String> list){
            data = list;
        }
        @NonNull
        @Override
        public SaltingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView text = new TextView(parent.getContext());
            SaltingAdapter.ViewHolder v = new SaltingAdapter.ViewHolder(text);
            return v;
        }

        @Override
        public void onBindViewHolder(@NonNull SaltingAdapter.ViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class  ViewHolder  extends RecyclerView.ViewHolder {
            public TextView textView;
            public ViewHolder(@NonNull TextView itemView) {
                super(itemView);
                textView = itemView;
            }
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.sausage_maker);
    }

    private void saltingCalculate(RecyclerView view){
        adapter = new SaltingAdapter(saltingUnit.calculate());
        view.setAdapter(adapter);
    }

    private double getValue(View view, int id, double defaultValue){
        double result;
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
