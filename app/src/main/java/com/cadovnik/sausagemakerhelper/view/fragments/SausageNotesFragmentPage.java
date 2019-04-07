package com.cadovnik.sausagemakerhelper.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.data.HeatingProcess;
import com.cadovnik.sausagemakerhelper.data.SaltingUnit;
import com.cadovnik.sausagemakerhelper.data.SausageNotes;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SausageNotesFragmentPage extends Fragment {
    private static SausageNotesFragmentPage instance = null;
    public static SausageNotesFragmentPage newInstance() {
        if (instance == null )
            instance = new SausageNotesFragmentPage();
        return instance;
    }
    private SausageNotes notes;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(this.getClass().getSimpleName(), "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.sausage_notes, container, false);
        RecyclerView rlay = view.findViewById(R.id.sausage_notes_page);
        notes = new SausageNotes(getContext());
        rlay.setAdapter(new SausageNotesFragmentPage.SausageNotesAdapter(notes));
        rlay.setLayoutManager( new LinearLayoutManager(getActivity()) );
        Log.d(this.getClass().getSimpleName(), "onCreateView: ");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getSimpleName(), "onDestroy: ");
    }

    public static class SausageNotesAdapter extends RecyclerView.Adapter<SausageNotesFragmentPage.SausageNotesAdapter.ViewHolder> {

        private SausageNotes notes;
        public SausageNotesAdapter(SausageNotes notes) {
            this.notes = notes;
        }

        @NonNull
        @Override
        public SausageNotesFragmentPage.SausageNotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.sausage_card, parent, false);
            SausageNotesFragmentPage.SausageNotesAdapter.ViewHolder v = new SausageNotesFragmentPage.SausageNotesAdapter.ViewHolder(card);
            return v;
        }

        @Override
        public void onBindViewHolder(@NonNull SausageNotesFragmentPage.SausageNotesAdapter.ViewHolder holder, int position) {
            TextView title = holder.cardView.findViewById(R.id.sausage_title);
            title.setText(notes.At(position).getName());
            TextView description = holder.cardView.findViewById(R.id.sausage_description);
            description.setText(notes.At(position).getDescription());
            holder.setSaltingInfo(notes.At(position).getSalting());
            holder.setHeatingInfo(notes.At(position).getHeating());
//            ImageView image = holder.cardView.findViewById(R.id.sausage_image);
//            Bitmap bitmap = notes.At(position).getBitmap();
//            holder.cardView.getViewTreeObserver().addOnGlobalLayoutListener(
//                    () -> {
//                        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, holder.cardView.getWidth(), image.getHeight(),false));
//                    });
        }

        @Override
        public int getItemCount() {
            return notes.getCount();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public MaterialCardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.sausage_card);

            }

            public void setSaltingInfo(SaltingUnit salting){

            }

            public void setHeatingInfo(HeatingProcess process){

            }
        }
    }
}
