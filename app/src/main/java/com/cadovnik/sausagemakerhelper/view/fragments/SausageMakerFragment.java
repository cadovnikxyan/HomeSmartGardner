package com.cadovnik.sausagemakerhelper.view.fragments;

import android.content.ContentValues;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.data.DataController;
import com.cadovnik.sausagemakerhelper.data.SaltingUnit;
import com.cadovnik.sausagemakerhelper.data.SausageNote;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SausageMakerFragment extends Fragment {

    private SaltingUnit saltingUnit;
    private SausageMakerFragmentAdapter adapter;
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
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.sausage_maker, container, false);
        FloatingActionsMenu menu = view.findViewById(R.id.multiple_actions);
        FloatingActionButton calculate = view.findViewById(R.id.salting_calculate);
        TextInputEditText salting = view.findViewById(R.id.salting_percent_value);
        salting.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        TextInputEditText nitrite_salting = view.findViewById(R.id.nitrite_salt_percent_value);
        nitrite_salting.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        RecyclerView result = view.findViewById(R.id.sausage_result);
        result.setHasFixedSize(true);
        adapter = new SausageMakerFragment.SausageMakerFragmentAdapter(new ArrayList<>());
        layoutManager = new LinearLayoutManager(getActivity());
        result.setLayoutManager(layoutManager);

        result.setAdapter(adapter);
        result.setLayoutManager( new LinearLayoutManager(getActivity()));
        saltingUnit = new SaltingUnit();

        view.findViewById(R.id.add_spice).setOnClickListener(v -> {
            adapter.addSpice(new Pair<>("",""));
        });

        calculate.setOnClickListener(v -> {
            saltingUnit = new SaltingUnit();
            saltingUnit.setWet_salting(getValue(view, R.id.dry_wet));
            saltingUnit.setNitrite_salting_percent(getValue(view, R.id.nitrite_salt_percent_value, Double.valueOf(R.string.nitrite_salt_default)));
            saltingUnit.setSalting_percent(getValue(view, R.id.salting_percent_value, Double.valueOf(R.string.salting_percent_default)));
            saltingUnit.setWeight_of_meat(getValue(view, R.id.meat_weight_value, 0));
            saltingUnit.setWith_phosphates(getValue(view, R.id.phosphates));
            saltingUnit.setWith_sodium_ascorbate(getValue(view, R.id.sodium_ascorbate));
            saltingUnit.calculate();
            adapter.addItems(fillResults(saltingUnit));
            menu.collapse();

        });
        FloatingActionButton save = view.findViewById(R.id.salting_save);
        save.setOnClickListener( v -> {
            TextInputEditText sausage_name = view.findViewById(R.id.sausage_name);
            SausageNote note = new SausageNote(sausage_name.getText().toString(), saltingUnit, null);
            note.setBitmap(BitmapFactory.decodeResource(getResources(),  R.raw.sausage_pic_2));
            TextInputEditText des = view.findViewById(R.id.sausage_description);
            note.setDescription(des.getText().toString());
            DataController controller = new DataController(getContext());
            ContentValues values = note.convert();
            note.insert(controller.getWritableDatabase(), values);
            controller.close();
            menu.collapse();
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.sausage_maker);
    }

    private List<Pair<String, String>> fillResults(SaltingUnit unit){
        List<Pair<String, String>> results = new ArrayList<>();

        if ( unit.isWet_salting() ){

            results.add(new Pair<>(getString(R.string.sausage_brine), String.valueOf(unit.getBrine())));
        }
        if ( unit.getNitrite_salting_percent() != 0 ) {
            results.add(new Pair<>(getString(R.string.sausage_nitrite_salt),  String.valueOf(unit.getNitrite_salt())));
        }
        if ( unit.isWith_phosphates() ) {
            results.add(new Pair<>(getString(R.string.sausage_phosphates), String.valueOf(unit.getPhosphates())));
        }
        if ( unit.isWith_sodium_ascorbate() ) {
            results.add(new Pair<>(getString(R.string.sausage_sodium_ascorbate), String.valueOf(unit.getSodium_ascorbate())));
        }

        results.add(new Pair<>(getString(R.string.sausage_rock_salt), String.valueOf(unit.getRock_salt())));
        return results;
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

    public static class SausageMakerFragmentAdapter extends RecyclerView.Adapter<SausageMakerFragment.SausageMakerFragmentAdapter.ViewHolder>{
        private List<Pair<String, String>> spices;

        public SausageMakerFragmentAdapter(List<Pair<String, String>> spices){
            this.spices = spices;
        }

        public void addSpice(Pair<String, String> spice){
            spices.add(spice);
            notifyDataSetChanged();

        }
        public void addItems(List<Pair<String, String>>  items){
            Set<Pair<String, String>> set = new HashSet<>(items);
            spices.addAll(set);
            notifyDataSetChanged();

        }
        @NonNull
        @Override
        public SausageMakerFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sausage_spice, parent, false);
            SausageMakerFragment.SausageMakerFragmentAdapter.ViewHolder v = new SausageMakerFragment.SausageMakerFragmentAdapter.ViewHolder(view);
            return v;
        }

        @Override
        public void onBindViewHolder(@NonNull SausageMakerFragmentAdapter.ViewHolder holder, int position) {
            Pair<String, String> spice = spices.get(position);
            TextInputEditText name = holder.view.findViewById(R.id.sausage_spice).findViewById(R.id.sausage_spice_name);
            name.setText(spice.first);
            TextInputEditText weight = holder.view.findViewById(R.id.sausage_spice).findViewById(R.id.sausage_spice_weight);
            String weightValue = String.format("%.2f", Double.valueOf(spice.second.isEmpty() ? "0" : spice.second));
            weight.setText(weightValue);
        }

        @Override
        public int getItemCount() {
            return spices.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            public View view;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
            }
        }
    }
}
