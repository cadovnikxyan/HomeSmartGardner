package com.cadovnik.homesmartgardner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class MainFragment extends Fragment {
    public static MainFragment newInstance(int index) {
        MainFragment f = new MainFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist. The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // isn't displayed. Note this isn't needed -- we could just
            // run the code below, where we would create and return the
            // view hierarchy; it would just never be used.
            return null;
        }

//        super.onCreate(savedInstanceState);
//        getActivity().setContentView(R.layout.content_main);
//
//        ScrollView scroller = new ScrollView(getActivity());
//        TextView text = new TextView(getActivity());
//        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                4, getActivity().getResources().getDisplayMetrics());
//        text.setPadding(padding, padding, padding, padding);
//        scroller.addView(text);
//        text.setText("Hello");
        return  inflater.inflate(R.layout.content_main, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");
    }


}
