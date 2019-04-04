package com.cadovnik.sausagemakerhelper.view.fragments;

import android.os.Bundle;
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

    public static SausageNotesArchiveFragmentPage instance = null;
    public static SausageNotesArchiveFragmentPage newInstance() {
        if (instance == null )
            instance = new SausageNotesArchiveFragmentPage();
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.sausage_notes, container, false);
        RecyclerView rlay = view.findViewById(R.id.sausage_notes_page);
        rlay.setAdapter(new SausageNotesArchiveFragmentPage.SausageNotesArchiveAdapter());
        rlay.setLayoutManager( new LinearLayoutManager(getActivity()) );
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
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