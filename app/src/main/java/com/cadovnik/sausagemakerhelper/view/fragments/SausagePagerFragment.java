package com.cadovnik.sausagemakerhelper.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SausagePagerFragment extends Fragment {
    private static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 10;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public static SausagePagerFragment newInstance() {
        return new SausagePagerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final List<Object> items = new ArrayList<>();

        for (int i = 0; i < ITEM_COUNT; ++i) {
            items.add(new Object());
        }


        //setup materialviewpager

        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        mRecyclerView.setAdapter(new TestRecyclerViewAdapter(items));
    }

    public static class TestRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Object> contents;

        static final int TYPE_HEADER = 0;
        static final int TYPE_CELL = 1;

        public TestRecyclerViewAdapter(List<Object> contents) {
            this.contents = contents;
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return TYPE_HEADER;
                default:
                    return TYPE_CELL;
            }
        }

        @Override
        public int getItemCount() {
            return contents.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            switch (viewType) {
                default: {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_card_big, parent, false);
                    return new RecyclerView.ViewHolder(view) {
                    };
                }
            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case TYPE_HEADER:
                    break;
                case TYPE_CELL:
                    break;
            }
        }
    }
}
