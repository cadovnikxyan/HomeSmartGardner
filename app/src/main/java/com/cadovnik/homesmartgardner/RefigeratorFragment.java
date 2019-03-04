package com.cadovnik.homesmartgardner;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RefigeratorFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private GraphView mGraph;
    private static final String PATH_TO_SERVER = "http://www.cadovnik.fvds.ru:9999/sensors/historical-sensordata";

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
        View view = inflater.inflate(R.layout.refrigerate_chart, container, false);
        mGraph = (GraphView) view.findViewById(R.id.graph);
        Button loadTextButton = (Button)view.findViewById(R.id.load_file_from_server);
        loadTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefigeratorFragment.DownloadFilesTask downloadFilesTask = new RefigeratorFragment.DownloadFilesTask();
                downloadFilesTask.execute();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Refigerator");
    }

    private class DownloadFilesTask extends AsyncTask<URL, Void, JSONArray> {
        protected JSONArray doInBackground(URL... urls) {
            return downloadRemoteTextFileContent();
        }
        protected void onPostExecute(JSONArray result) {
            if(result != null){
                createLineGraph(result);
            }
        }
    }
    private void createLineGraph(JSONArray jArray){
        for (int i = 0; i < jArray.length(); i++) {

            try
            {
                JSONObject jObject = jArray.getJSONObject(i);
                String sensName = jObject.getString("sensorName");
                String sensKind = jObject.getString("sensorKind");
                JSONArray vals = jObject.getJSONArray("values");

                DataPoint[] dataPoints = new DataPoint[vals.length()];
                for (int j = 0; i < vals.length(); i++){
                    JSONObject obj = vals.getJSONObject(j);
                    dataPoints[i] = new DataPoint(new Date( obj.getLong("x")), obj.getDouble("y"));
                }
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                mGraph.addSeries(series);

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }


    } // End Loop

    }
    private void createBarChartGraph(List<String[]> result){
        DataPoint[] dataPoints = new DataPoint[result.size()];
        for (int i = 0; i < result.size(); i++){
            String [] rows = result.get(i);
            Log.d(TAG, "Output " + Integer.parseInt(rows[0]) + " " + Integer.parseInt(rows[1]));
            dataPoints[i] = new DataPoint(Integer.parseInt(rows[0]), Integer.parseInt(rows[1]));
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(dataPoints);
        mGraph.addSeries(series);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });
        series.setSpacing(50);
    }
    private JSONArray downloadRemoteTextFileContent(){
        URL mUrl = null;
        JSONArray jArray = new JSONArray();
        try {
            mUrl = new URL(PATH_TO_SERVER);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        };
        try {
            URLConnection connection = mUrl.openConnection();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line);
            }
            bReader.close();
            String result = sBuilder.toString();
            jArray = new JSONArray(result);

        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        } catch (Exception e) {
            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
        }
        return jArray;
    }
}
