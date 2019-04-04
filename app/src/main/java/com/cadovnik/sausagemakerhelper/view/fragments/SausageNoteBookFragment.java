package com.cadovnik.sausagemakerhelper.view.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.view.MainActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SausageNoteBookFragment extends Fragment {
    private static SausageNoteBookFragment instance = null;
    public static SausageNoteBookFragment newInstance(){
        if ( instance == null )
            instance = new SausageNoteBookFragment();

        return instance;
    }

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
        tabs.setupWithViewPager(pager);
        FloatingActionsMenu menu = view.findViewById(R.id.multiple_actions);
        FloatingActionButton create_sausage = view.findViewById(R.id.create_sausage);
        create_sausage.setOnClickListener(v -> {
            menu.collapse();
            ((MainActivity)getActivity()).displaySelectedScreen(R.id.sausage_maker);
        });
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.sausage_notes);
    }

    public static class SausageNoteBookFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private Context context;
        private String tabTitles[];
        private int[] imageResId = {
                R.drawable.salamis,
                R.drawable.calendar,
                R.drawable.archive,
        };
        public SausageNoteBookFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            tabTitles = new String[] {context.getResources().getString(R.string.sausage_notes), context.getResources().getString(R.string.sausage_calendar), context.getResources().getString(R.string.sausage_archive)};
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return SausageNotesFragmentPage.newInstance();
                case 1:
                    return SausageCalendarFragment.newInstance();
                case 2:
                    return SausageNotesArchiveFragmentPage.newInstance();
            }
            return null;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = context.getResources().getDrawable(imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(tabTitles[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }
}
