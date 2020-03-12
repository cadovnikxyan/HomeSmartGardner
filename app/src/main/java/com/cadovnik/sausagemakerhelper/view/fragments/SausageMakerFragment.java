package com.cadovnik.sausagemakerhelper.view.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.data.DataController;
import com.cadovnik.sausagemakerhelper.data.SaltingUnit;
import com.cadovnik.sausagemakerhelper.data.SausageNote;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SausageMakerFragment extends Fragment {

    private SaltingUnit saltingUnit;
    private SausageMakerFragmentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView result = null;
    private  View resultLabel = null;
    private String[] sausageOptionsNames;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sausageOptionsNames = new String[]{getString(R.string.sausage_options),getString(R.string.dry_wet_salting), getString(R.string.phosphates), getString(R.string.sodium_ascorbate)};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.sausage_calculator, container, false);
        FloatingActionsMenu menu = view.findViewById(R.id.multiple_actions);
        FloatingActionButton calculate = view.findViewById(R.id.salting_calculate);
        TextInputEditText salting = view.findViewById(R.id.salting_percent_value);
        salting.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        TextInputEditText nitrite_salting = view.findViewById(R.id.nitrite_salt_percent_value);
        nitrite_salting.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        result = view.findViewById(R.id.sausage_result);
        result.setHasFixedSize(true);
        result.setVisibility(View.INVISIBLE);
        resultLabel = view.findViewById(R.id.salting_result_header);
        resultLabel.setVisibility(View.INVISIBLE);
        adapter = new SausageMakerFragment.SausageMakerFragmentAdapter(new ArrayList<>(), new ArrayList<>(), getContext());
        layoutManager = new LinearLayoutManager(getActivity());
        result.setLayoutManager(layoutManager);
        result.setAdapter(adapter);
        result.setLayoutManager( new LinearLayoutManager(getActivity()));
        saltingUnit = new SaltingUnit();

        Spinner spinner = view.findViewById(R.id.sausage_options);

        ArrayList<SausageOption> listOptions = new ArrayList<>();

        for (int i = 0; i < sausageOptionsNames.length; i++) {
            SausageOption option = new SausageOption();
            option.setTitle(sausageOptionsNames[i]);
            option.setSelected(false);
            listOptions.add(option);
        }

        SausageOptionAdapter sausageOptionAdapter = new SausageOptionAdapter(getContext(), 0, listOptions);
        spinner.setAdapter(sausageOptionAdapter);

        view.findViewById(R.id.add_spice).setOnClickListener(v -> {
            adapter.addSpice(new Pair<>("",""));
            menu.collapse();
        });

        calculate.setOnClickListener(v -> {
            saltingUnit = new SaltingUnit();
            saltingUnit.setWet_salting(listOptions.get(1).isSelected());
            saltingUnit.setNitrite_salting_percent(getValue(view, R.id.nitrite_salt_percent_value, Double.valueOf(R.string.nitrite_salt_default)));
            saltingUnit.setSalting_percent(getValue(view, R.id.salting_percent_value, Double.valueOf(R.string.salting_percent_default)));
            saltingUnit.setWeight_of_meat(getValue(view, R.id.meat_weight_value, 0));
            saltingUnit.setWith_phosphates(listOptions.get(2).isSelected());
            saltingUnit.setWith_sodium_ascorbate(listOptions.get(3).isSelected());
            saltingUnit.calculate();
            resultLabel.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
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
    private static int getIconID(String item, Context context){
        if (item.equals(context.getString(R.string.sausage_nitrite_salt))){
            return R.drawable.nitrite_salt;
        }else if (item.equals(context.getString(R.string.sausage_rock_salt))){
            return R.drawable.rock_salt;
        }else if (item.equals(context.getString(R.string.sausage_brine))) {
            return R.drawable.brine;
        }else if (item.equals(context.getString(R.string.sausage_phosphates)) || item.equals(context.getString(R.string.sausage_sodium_ascorbate)) ) {
            return R.drawable.additives;
        }else{
            return R.drawable.spices;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        if ( unit.getRock_salt() != 0 ){
            results.add(new Pair<>(getString(R.string.sausage_rock_salt), String.valueOf(unit.getRock_salt())));
        }
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

    public static class SausageMakerFragmentAdapter extends RecyclerView.Adapter<SausageMakerFragment.SausageMakerFragmentAdapter.ViewHolder>{
        private List<Pair<String, String>> spices;
        private List<Pair<String, String>> saltings;
        private  Context context;

        public SausageMakerFragmentAdapter(List<Pair<String, String>> spices, List<Pair<String, String>> saltings, Context context){
            this.spices = spices;
            this.saltings = saltings;
            this.context = context;
        }

        public void addSpice(Pair<String, String> spice){
            spices.add(spice);
            notifyDataSetChanged();

        }
        public List<Pair<String, String>> getSpices(){return spices;}

        public void addItems(List<Pair<String, String>>  items){
            saltings = items;
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
            List<Pair<String, String>> list = new ArrayList<>();
            list.addAll(spices);
            list.addAll(saltings);
            Pair<String, String> item = list.get(position);

            boolean isReadOnly = true;
            if ( saltings.contains(item) ){
                isReadOnly = false;
            }
            bindView(holder, item, isReadOnly);
        }

        private void bindView(@NonNull SausageMakerFragmentAdapter.ViewHolder holder,  Pair<String, String> item, boolean isReadOnly){
            TextInputEditText name = holder.view.findViewById(R.id.sausage_spice).findViewById(R.id.sausage_spice_name);
            name.setText(item.first);
            name.setEnabled(isReadOnly);
            name.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(SausageMakerFragment.getIconID(item.first, context)),null, null,null);
            TextInputEditText weight = holder.view.findViewById(R.id.sausage_spice).findViewById(R.id.sausage_spice_weight);
            String weightValue = String.format(Locale.ENGLISH,"%.2f", Double.valueOf(item.second.isEmpty() ? "0" : item.second));
            weight.setText(weightValue);
            weight.setEnabled(isReadOnly);
        }

        @Override
        public int getItemCount() {
            return spices.size() + saltings.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            public View view;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
            }
        }
    }

    public static class SausageOption {
        private String title;
        private boolean selected;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public static class SausageOptionAdapter extends ArrayAdapter<SausageOption> {
        private ArrayList<SausageOption> listState;
        private boolean isFromView = false;

        public SausageOptionAdapter(Context context, int resource, List<SausageOption> objects) {
            super(context, resource, objects);
            this.listState = (ArrayList<SausageOption>) objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);

        }

        public View getCustomView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
                convertView = layoutInflator.inflate(R.layout.sausage_option_item, null);
                holder = new ViewHolder();
                holder.text = convertView.findViewById(R.id.option_name);
                holder.checkBox = convertView.findViewById(R.id.option_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(listState.get(position).getTitle());
            holder.text.setOnClickListener(v -> holder.checkBox.setChecked( !holder.checkBox.isChecked()));
            isFromView = true;
            holder.checkBox.setChecked(listState.get(position).isSelected());
            isFromView = false;

            if ( position == 0)
                holder.checkBox.setVisibility(View.INVISIBLE);
            else
                holder.checkBox.setVisibility(View.VISIBLE);

            holder.checkBox.setTag(position);
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView text;
            private CheckBox checkBox;
        }
    }
}
