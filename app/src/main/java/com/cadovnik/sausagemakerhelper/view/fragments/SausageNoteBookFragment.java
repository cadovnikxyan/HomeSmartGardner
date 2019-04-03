package com.cadovnik.sausagemakerhelper.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SausageNoteBookFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.sausage_notebook, container, false);
        TabLayout tabs = view.findViewById(R.id.sausage_notes_tabs);
        ViewPager pager = view.findViewById(R.id.sausage_notes_pager);
        pager.setAdapter(new SausageNoteBookFragmentPagerAdapter(getFragmentManager(), getContext()));
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.sausage_notes);
    }

    public static class SausageNoteBookFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private Context context;
        private String tabTitles[] ;

        public SausageNoteBookFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            tabTitles = new String[] {context.getResources().getString(R.string.sausage_notes), context.getResources().getString(R.string.sausage_calendar)};
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return SausageNotesFragmentPage.newInstance();
                case 1:
                    return SausageCalendarFragment.newInstance();
            }
            return null;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
