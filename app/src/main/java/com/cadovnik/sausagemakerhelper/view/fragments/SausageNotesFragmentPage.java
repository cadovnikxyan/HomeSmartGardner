package com.cadovnik.sausagemakerhelper.view.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.data.DataController;
import com.cadovnik.sausagemakerhelper.data.HeatingProcess;
import com.cadovnik.sausagemakerhelper.data.SaltingUnit;
import com.cadovnik.sausagemakerhelper.data.SausageNotes;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import ru.rambler.libs.swipe_layout.SwipeLayout;

public class SausageNotesFragmentPage extends Fragment {
    private SausageNotes notes;

    public SausageNotesFragmentPage(){
        Log.d(this.getClass().getSimpleName(), "Constructor: ");
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        notes = new SausageNotes(getContext());
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
        Log.d(this.getClass().getSimpleName(), "onViewCreated: ");
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
            SwipeLayout swipeLayout = (SwipeLayout) holder.view;
            swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                @Override
                public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                }

                @Override
                public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                    if ( !moveToRight ){
                        Toast.makeText(swipeLayout.getContext(),
                                "Sausage deleted!",
                                Toast.LENGTH_SHORT)
                                .show();
                        DataController controller = new DataController(holder.view.getContext());
                        notes.At(position).removeRow(controller.getWritableDatabase());
                        notes.RemoveAt(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, notes.getCount());
                        controller.close();
                    }
                }

                @Override
                public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                }

                @Override
                public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                }
            });
            TextView title = holder.cardView.findViewById(R.id.sausage_title);
            title.setText(notes.At(position).getName());
            TextView description = holder.cardView.findViewById(R.id.sausage_description);
            String desStr = notes.At(position).getDescription();
            if ( desStr !=null && !desStr.isEmpty() )
                description.setText(desStr);
            holder.setSaltingInfo(notes.At(position).getSalting());
            holder.setHeatingInfo(notes.At(position).getHeating());
            Bitmap bitmap = notes.At(position).getBitmap();
            holder.cardView.getViewTreeObserver().addOnGlobalLayoutListener(
                    () -> {
                        ImageView image = holder.cardView.findViewById(R.id.sausage_image);
                        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, holder.cardView.getWidth(), image.getHeight(),false));
                    });
        }

        @Override
        public int getItemCount() {
            return notes.getCount();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public MaterialCardView cardView;
            public View view;
            private View extraLayout;
            SaltingUnit saltingUnit;
            HeatingProcess heatingProcess;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.sausage_card);
                extraLayout = cardView.findViewById(R.id.info_card);
                cardView.setOnClickListener(v -> {

                        if (extraLayout.getVisibility() == View.GONE) {
                            extraLayout.setVisibility(View.VISIBLE);
                            ((TextInputEditText)cardView.findViewById(R.id.card_extra_nitrite_salt)).setText(String.format(Locale.ENGLISH,"%.2f",saltingUnit.getNitrite_salt()));
                            ((TextInputEditText)cardView.findViewById(R.id.card_extra_rock_salt)).setText(String.format(Locale.ENGLISH,"%.2f",saltingUnit.getRock_salt()));
                            ((TextInputEditText)cardView.findViewById(R.id.card_extra_weight_meat)).setText(String.format(Locale.ENGLISH,"%.2f",saltingUnit.getWeight_of_meat()));
                            TextInputEditText brine =  cardView.findViewById(R.id.card_extra_brine);
                            if ( saltingUnit.isWet_salting() ){
                                brine.setText(String.format(Locale.ENGLISH,"%.2f",saltingUnit.getBrine()));
                                brine.setVisibility(View.VISIBLE);
                            }else{
                                brine.setVisibility(View.GONE);
                            }

                            TextInputEditText phosphates =  cardView.findViewById(R.id.card_extra_phosphates);
                            if ( saltingUnit.isWith_phosphates() ){
                                phosphates.setText(String.format(Locale.ENGLISH,"%.2f",saltingUnit.getPhosphates()));
                                phosphates.setVisibility(View.VISIBLE);
                            }else{
                                phosphates.setVisibility(View.GONE);
                            }

                            TextInputEditText sodium_ascorbate =  cardView.findViewById(R.id.card_extra_sodium_ascorbate);
                            if ( saltingUnit.isWith_sodium_ascorbate() ){
                                sodium_ascorbate.setText(String.format(Locale.ENGLISH,"%.2f",saltingUnit.getSodium_ascorbate()));
                                sodium_ascorbate.setVisibility(View.VISIBLE);
                            }else{
                                sodium_ascorbate.setVisibility(View.GONE);
                            }


                        } else {
                            extraLayout.setVisibility(View.GONE);
                        }
                    TransitionManager.beginDelayedTransition((ViewGroup) view, new AutoTransition());
                });
                view = itemView;
            }

            public void setSaltingInfo(SaltingUnit salting){
                this.saltingUnit = salting;
            }

            public void setHeatingInfo(HeatingProcess process){
                this.heatingProcess = process;
            }
        }
    }
}
