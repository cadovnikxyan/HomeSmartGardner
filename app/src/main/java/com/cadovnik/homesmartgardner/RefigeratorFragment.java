package com.cadovnik.homesmartgardner;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.formatter.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class RefigeratorFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static SSLContext context;
//    private static HostnameVerifier hostnameVerifier;
    private LineChart lineChart;
    private TableLayout lastedTableLayout = null;
    private TextView dateFrom = null;
    private TextView dateTo = null;
    Calendar dateCFrom =  null;
    Calendar dateCTo =  null;
    private static final String ServiceHistDataUrl = "https://cadovnik.fvds.ru:9999/sensors/historical-sensordata";
    private  static  final String ServiceLatestDataUrl =  "https://cadovnik.fvds.ru:9999/sensors/latest-sensordata/";
    private  static  final String Hostname = "cadovnik.fvds.ru";
    private static final String DateFormat = "MM/dd/yyyy";
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
            return null;
        }
        View view = inflater.inflate(R.layout.refrigerate_chart, container, false);
        trustAllHosts(getActivity().getResources());

        lastedTableLayout = (TableLayout) view.findViewById(R.id.lasted_data_table);
        lineChart = (LineChart) view.findViewById(R.id.chart);
        dateFrom = (TextView) view.findViewById(R.id.dateFrom);
        dateTo = (TextView) view.findViewById(R.id.dateTo);

        setUpChart();
        setUpDates();

        Button loadHistory = (Button)view.findViewById(R.id.load_file_from_server);
        loadHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HostoricalData().execute();
            }
        });

        Button loadLasted = (Button)view.findViewById(R.id.get_lasted);
        loadLasted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LastedData().execute();
            }
        });
        loadHistory.callOnClick();
        loadLasted.callOnClick();
        return view;
    }

    private void setUpChart(){

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new DateFormatter());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    private void setUpDates(){
        dateCFrom = Calendar.getInstance();
        dateCTo = Calendar.getInstance();
        final String currentDate = new SimpleDateFormat(DateFormat).format(Calendar.getInstance().getTime());
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), dFrom,
                        dateCFrom.get(Calendar.YEAR),
                        dateCFrom.get(Calendar.MONTH),
                        dateCFrom.get(Calendar.DAY_OF_MONTH))
                        .show();

            }
        });
        dateFrom.setText(currentDate);
        dateTo.setText(currentDate);
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), dTo,
                        dateCTo.get(Calendar.YEAR),
                        dateCTo.get(Calendar.MONTH),
                        dateCTo.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Refigerator");
    }

    DatePickerDialog.OnDateSetListener dFrom = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCFrom.set(Calendar.YEAR, year);
            dateCFrom.set(Calendar.MONTH, monthOfYear);
            dateCFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setFromDate();
        }
    };
    DatePickerDialog.OnDateSetListener dTo = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCTo.set(Calendar.YEAR, year);
            dateCTo.set(Calendar.MONTH, monthOfYear);
            dateCTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setToDate();
        }
    };
    public void setFromDate(){
        dateFrom.setText(new SimpleDateFormat(DateFormat).format(dateCFrom.getTimeInMillis()).toString());
    }

    public void setToDate(){
        dateTo.setText(new SimpleDateFormat(DateFormat).format(dateCTo.getTimeInMillis()).toString());
    }
    private class DateFormatter implements IAxisValueFormatter {

        private SimpleDateFormat mFormat;

        public DateFormatter() {
            mFormat = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value);
        }
    }

    private class HostoricalData extends AsyncTask<URL, Void, JSONArray> {
        protected JSONArray doInBackground(URL... urls) {
            String timestampFrom = "";
            String timestampTo = "";
            try{
                timestampFrom = String.valueOf(new SimpleDateFormat("MM/dd/yyyy").parse(dateFrom.getText().toString()).getTime());
                timestampTo = String.valueOf(new SimpleDateFormat("MM/dd/yyyy").parse(dateTo.getText().toString()));
            }
            catch (ParseException e) {
                Log.e("ParseException", "Error: " + e.toString());
            }
            return downloadRemoteTextFileContent(ServiceHistDataUrl  + "?fromtimestamp=" + timestampFrom + "&totimestamp=" + timestampTo);
        }
        protected void onPostExecute(JSONArray result) {
            if(result != null){
                createLineGraph(result);
            }
        }
    }

    private  class LastedData extends  AsyncTask<URL, Void, JSONArray>{
        @Override
        protected JSONArray doInBackground(URL... urls) {
            return downloadRemoteTextFileContent(ServiceLatestDataUrl);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray != null){
                for (int i = 0; i < jsonArray.length(); i++) {
                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT));
                    try{
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String sensName = jObject.getString("sensorName");
                        String sensKind = jObject.getString("sensorKind");
                        JSONArray vals = jObject.getJSONArray("values");
                        TextView viewSName = new TextView(getContext());
                        viewSName.setText(sensName);
                        TextView viewSKind = new TextView(getContext());
                        viewSKind.setText(sensKind);
                        TextView viewSValDate = new TextView(getContext());
                        viewSValDate.setText(new SimpleDateFormat("hh:mm:ss ").format(vals.getJSONObject(0).getLong("x")));
                        TextView viewSVal = new TextView(getContext());
                        viewSVal.setText(vals.getJSONObject(0).getString("y"));
                        tableRow.addView(viewSName);
                        tableRow.addView(viewSKind);
                        tableRow.addView(viewSValDate);
                        tableRow.addView(viewSVal);
                        lastedTableLayout.addView(tableRow, i + 1);
                    }
                    catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                    }
                }
            }
        }
    }

    private void createLineGraph(JSONArray jArray) {
        lineChart.clear();
        try {
            LineData lineData = new LineData();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String sensName = jObject.getString("sensorName");
                String sensKind = jObject.getString("sensorKind");
                JSONArray vals = jObject.getJSONArray("values");

                List<Entry> entries = new ArrayList<Entry>();

                for (int j = 0; j < vals.length(); j++) {
                    JSONObject obj = vals.getJSONObject(j);
                    float x = (float) obj.getDouble("x");
                    float y = (float) obj.getDouble("y");
                    entries.add(new Entry(x, y));
                }
                LineDataSet dataSet = new LineDataSet(entries, sensKind);
                dataSet.setLabel(sensName);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setColor(ColorTemplate.getHoloBlue());
                dataSet.setValueTextColor(ColorTemplate.getHoloBlue());
                dataSet.setLineWidth(1.5f);
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setFillAlpha(65);
                dataSet.setFillColor(ColorTemplate.getHoloBlue());
                dataSet.setHighLightColor(Color.rgb(244, 117, 117));
                dataSet.setDrawCircleHole(false);
                lineData.addDataSet(dataSet);
                }
            lineChart.setData(lineData);
            lineChart.invalidate();
        } catch(JSONException e){
            Log.e("JSONException", "Error: " + e.toString());
        }
    }
    @SuppressLint("TrulyRandom")
    private static void trustAllHosts(Resources res) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = res.openRawResource(R.raw.certificate);
            Certificate ca = null;
            ca = cf.generateCertificate(caInput);

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        }catch (CertificateException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (KeyStoreException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (SecurityException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (NoSuchAlgorithmException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (KeyManagementException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (IOException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }

    }

    private JSONArray downloadRemoteTextFileContent(String reqURL){
        URL mUrl = null;
        JSONArray jArray = null;
        try {
            mUrl = new URL(reqURL);
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", "Error: " + e.toString());
        };
        try {
            HttpsURLConnection connection = (HttpsURLConnection) mUrl.openConnection();
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals(Hostname);
                }
            };
            connection.setHostnameVerifier(hostnameVerifier);
            connection.setSSLSocketFactory(context.getSocketFactory());
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
