package com.cadovnik.sausagemakerhelper.view.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.data.DataController;
import com.cadovnik.sausagemakerhelper.data.SaltingUnit;
import com.cadovnik.sausagemakerhelper.data.SausageNote;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ValidFragment")
public class SaveSausageDialog extends DialogFragment {

    private SaltingUnit unit;
    private DataController db;
    SausageNote note;

    public SaveSausageDialog(SaltingUnit unit){
        this.unit = unit;
        db = new DataController(getContext());
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.save_sausage_dialog, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.sausage_spices_view);
        SaveSausageDialogAdapter adapter = new SaveSausageDialogAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle(R.string.save_sausage_dialog_title);
        dialogView.findViewById(R.id.add_spice).setOnClickListener(v -> {
            adapter.addSpice(new Pair<>("",""));
        });
        builder.setPositiveButton("Save",
                (dialog, whichButton) -> {
                    TextInputEditText text = dialogView.findViewById(R.id.sausage_name);
                    note = new SausageNote(text.getText().toString(), unit, null);
//                    note.setBitmap(BitmapFactory.decodeResource(getResources(),  R.raw.sausage_pic));
                    TextInputEditText des = dialogView.findViewById(R.id.sausage_description);
                    note.setDescription(des.getText().toString());
                    DataController controller = new DataController(getContext());
                    ContentValues values = note.convert();
                    note.insert(controller.getWritableDatabase(), values);
                    controller.close();
                }
        )
                .setNegativeButton("Cancel",
                        (dialog, whichButton) -> dialog.dismiss());
        return builder.create();
    }

    public static class SaveSausageDialogAdapter extends RecyclerView.Adapter<SaveSausageDialog.SaveSausageDialogAdapter.ViewHolder>{
        private List<Pair<String, String>> spices;
        public SaveSausageDialogAdapter(List<Pair<String, String>> spices){
            this.spices = spices;
        }
        public void addSpice(Pair<String, String> spice){
            spices.add(spice);
            notifyDataSetChanged();

        }
        @NonNull
        @Override
        public SaveSausageDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sausage_spice, parent, false);
            SaveSausageDialog.SaveSausageDialogAdapter.ViewHolder v = new SaveSausageDialog.SaveSausageDialogAdapter.ViewHolder(view);
            return v;
        }

        @Override
        public void onBindViewHolder(@NonNull SaveSausageDialogAdapter.ViewHolder holder, int position) {
            Pair<String, String> spice = spices.get(position);
            TextInputEditText name = holder.view.findViewById(R.id.sausage_spice).findViewById(R.id.sausage_spice_name);
            name.setText(spice.first);
            TextInputEditText weight = holder.view.findViewById(R.id.sausage_spice).findViewById(R.id.sausage_spice_weight);
            weight.setText(spice.second);
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
