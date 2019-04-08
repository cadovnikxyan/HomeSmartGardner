package com.cadovnik.sausagemakerhelper.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SausageNotesArchiveFragmentPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        Log.d(this.getClass().getSimpleName(), "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.sausage_notes, container, false);
        RecyclerView rlay = view.findViewById(R.id.sausage_notes_page);
        rlay.setAdapter(new SausageNotesArchiveFragmentPage.SausageNotesArchiveAdapter());
        rlay.setLayoutManager( new LinearLayoutManager(getActivity()) );
        Log.d(this.getClass().getSimpleName(), "onCreateView: ");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        Log.d(this.getClass().getSimpleName(), "onViewCreated: ");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getSimpleName(), "onDestroy: ");
    }

    public static class SausageNotesArchiveAdapter extends RecyclerView.Adapter<SausageNotesArchiveFragmentPage.SausageNotesArchiveAdapter.ViewHolder> {

        public SausageNotesArchiveAdapter( ) {
        }

        @NonNull
        @Override
        public SausageNotesArchiveFragmentPage.SausageNotesArchiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.sausage_card, parent, false);
            SausageNotesArchiveFragmentPage.SausageNotesArchiveAdapter.ViewHolder v = new SausageNotesArchiveFragmentPage.SausageNotesArchiveAdapter.ViewHolder(card);
            return v;
        }

        @Override
        public void onBindViewHolder(@NonNull SausageNotesArchiveFragmentPage.SausageNotesArchiveAdapter.ViewHolder holder, int position) {
//            holder.cardView.setText(data.get(position));
        }


        @Override
        public int getItemCount() {
            return 5;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public MaterialCardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.sausage_card);
            }
        }
    }
}
