package com.cadovnik.sausagemakerhelper.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;
import com.github.florent37.materialviewpager.MaterialViewPager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SausageNoteBookFragment extends Fragment {
    MaterialViewPager pager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.sausage_notebook, container, false);
        pager = view.findViewById(R.id.noteBook_pager);
        pager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()){

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Fragment getItem(int position) {
                return SausagePagerFragment.newInstance();
            }
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                        return "Selection";
                    case 1:
                        return "Actualités";
                    case 2:
                        return "Professionnel";
                    case 3:
                        return "Divertissement";
                }
                return "";
            }
        });
        pager.getViewPager().setOffscreenPageLimit(pager.getViewPager().getAdapter().getCount());
        pager.getPagerTitleStrip().setViewPager(pager.getViewPager());
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.sausage_notes);
    }

}
