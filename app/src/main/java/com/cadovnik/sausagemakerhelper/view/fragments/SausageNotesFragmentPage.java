package com.cadovnik.sausagemakerhelper.view.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.data.DataController;
import com.cadovnik.sausagemakerhelper.data.HeatingProcess;
import com.cadovnik.sausagemakerhelper.data.SaltingUnit;
import com.cadovnik.sausagemakerhelper.data.SausageNotes;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import ru.rambler.libs.swipe_layout.SwipeLayout;

public class SausageNotesFragmentPage extends Fragment {
    private SausageNotes notes;

    public SausageNotesFragmentPage(){
//        Log.d(this.getClass().getSimpleName(), "Constructor: ");
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        notes = new SausageNotes(getContext());
//        Log.d(this.getClass().getSimpleName(), "onCreate: ");
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
//        Log.d(this.getClass().getSimpleName(), "onCreateView: ");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Log.d(this.getClass().getSimpleName(), "onViewCreated: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(this.getClass().getSimpleName(), "onDestroy: ");
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
                            fillView(R.id.card_extra_weight_meat, saltingUnit.getWeight_of_meat(), true);
                            fillView(R.id.card_extra_rock_salt, saltingUnit.getRock_salt(), true);
                            fillView(R.id.card_extra_nitrite_salt, saltingUnit.getNitrite_salt(), true);
                            fillView(R.id.card_extra_brine, saltingUnit.getBrine(), saltingUnit.isWet_salting());
                            fillView(R.id.card_extra_phosphates, saltingUnit.getPhosphates(), saltingUnit.isWith_phosphates());
                            fillView(R.id.card_extra_sodium_ascorbate, saltingUnit.getSodium_ascorbate(), saltingUnit.isWith_sodium_ascorbate());
                        } else {
                            extraLayout.setVisibility(View.GONE);
                        }
                    TransitionManager.beginDelayedTransition((ViewGroup) view, new AutoTransition());
                });
                view = itemView;
            }

            private void fillView(int id, double value, boolean visible){
                TextInputEditText view = cardView.findViewById(id);
                if ( view == null )
                    return;

                if ( visible ){
                    view.setText(String.format(Locale.ENGLISH,"%.2f", value));
                    view.setVisibility(View.VISIBLE);
                }else{
                    view.setVisibility(View.GONE);
                }
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
