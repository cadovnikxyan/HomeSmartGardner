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

public class SausageNotesFragmentPage extends Fragment {
    public static SausageNotesFragmentPage newInstance() {
        SausageNotesFragmentPage fragment = new SausageNotesFragmentPage();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.sausage_notes, container, false);
        RecyclerView rlay = view.findViewById(R.id.sausage_notes_page);
        rlay.setAdapter(new SausageNotesFragmentPage.SausageNotesAdapter());
        rlay.setLayoutManager( new LinearLayoutManager(getActivity()) );
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
//        getActivity().setTitle(R.string.sausage_notes);
    }

    public static class SausageNotesAdapter extends RecyclerView.Adapter<SausageNotesFragmentPage.SausageNotesAdapter.ViewHolder> {

        public SausageNotesAdapter( ) {
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
