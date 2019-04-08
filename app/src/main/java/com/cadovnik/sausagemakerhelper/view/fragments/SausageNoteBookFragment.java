package com.cadovnik.sausagemakerhelper.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.view.MainActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SausageNoteBookFragment extends Fragment {

    protected static String tabTitles[];
    private SausageNoteBookFragmentPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        tabTitles = new String[] {getResources().getString(R.string.sausage_notes), getResources().getString(R.string.sausage_calendar), getResources().getString(R.string.sausage_archive)};
        adapter = new SausageNoteBookFragmentPagerAdapter(getChildFragmentManager(), getContext());
        Log.d(this.getClass().getSimpleName(), "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.sausage_notebook, container, false);
        TabLayout tabs = view.findViewById(R.id.sausage_notes_tabs);
        ViewPager pager = view.findViewById(R.id.sausage_notes_pager);
        tabs.setupWithViewPager(pager);
        pager.setAdapter(adapter);
        tabs.getTabAt(0).setIcon(R.drawable.salamis);
        tabs.getTabAt(1).setIcon(R.drawable.calendar);
        tabs.getTabAt(2).setIcon(R.drawable.archive);
        FloatingActionsMenu menu = view.findViewById(R.id.multiple_actions);
        FloatingActionButton create_sausage = view.findViewById(R.id.create_sausage);
        create_sausage.setOnClickListener(v -> {
            menu.collapse();
            ((MainActivity)getActivity()).displaySelectedScreen(R.id.sausage_maker);
        });

        Log.d(this.getClass().getSimpleName(), "onCreateView: ");
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.sausage_notes);
        Log.d(this.getClass().getSimpleName(), "onViewCreated: ");

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(this.getClass().getSimpleName(), "onDestroy: ");
    }

    public static class SausageNoteBookFragmentPagerAdapter extends FragmentPagerAdapter {
        private Context context;
        public SausageNoteBookFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;

        }

        @Override
        public int getCount() {
            return SausageNoteBookFragment.tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new SausageNotesFragmentPage();
                case 1:
                    return new SausageCalendarFragment();
                case 2:
                    return new SausageNotesArchiveFragmentPage();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return SausageNoteBookFragment.tabTitles[position];
        }
    }
}
