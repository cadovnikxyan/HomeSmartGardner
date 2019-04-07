package com.cadovnik.sausagemakerhelper.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cadovnik.sausagemakerhelper.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SausageCalendarFragment extends Fragment {

    private static SausageCalendarFragment instance = null;
    public static SausageCalendarFragment newInstance() {
        if (instance == null )
            instance = new SausageCalendarFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(this.getClass().getSimpleName(), "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.sausage_calendar, container, false);
        Log.d(this.getClass().getSimpleName(), "onCreateView: ");
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
//        getActivity().setTitle(R.string.sausage_calendar);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getSimpleName(), "onDestroy: ");
    }

}
