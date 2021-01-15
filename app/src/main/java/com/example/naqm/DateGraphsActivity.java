package com.example.naqm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.core.utils.OrdinalZoom;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;

public class DateGraphsActivity extends BaseActivity{
    public List<String> date = new ArrayList<>();
    public List<String> time = new ArrayList<>();
    public List<Double> nh3 = new ArrayList<>();
    public List<Double> co = new ArrayList<>();
    public List<Double> no2 = new ArrayList<>();
    public List<Integer> so2 = new ArrayList<>();
    public List<Integer> dust = new ArrayList<>();
    public List<Double> ch4 = new ArrayList<>();
    public List<Integer> temperature = new ArrayList<>();
    public List<Integer> humidity = new ArrayList<>();

    int s = 0;

    List<DataEntry> seriesDataNh3 = new ArrayList<>();
    List<DataEntry> seriesDataCo = new ArrayList<>();
    List<DataEntry> seriesDataNo2 = new ArrayList<>();
    List<DataEntry> seriesDataSo2 = new ArrayList<>();
    List<DataEntry> seriesDataCh4 = new ArrayList<>();
    List<DataEntry> seriesDataDust = new ArrayList<>();

    ApolloClient apolloClient;

    EditText edittext;
    final Calendar myCalendar = Calendar.getInstance();

    AnyChartView anyChartView1;
    Set set1;
    Cartesian cartesian;

    AlertDialog d;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_graphs);
        super.onCreateToolbar(getString(R.string.date_wise_graphs));
        super.onCreateDrawer();
        SpotsDialog.Builder dialog = new SpotsDialog.Builder();
        dialog.setContext(this);
        dialog.setTheme(R.style.Progress_dialog);
        dialog.setCancelable(false);
        d = dialog.build();
        d.show();
        //data retrieval pre-processing
        //chart setup
        anyChartView1 = findViewById(R.id.chartView1);
        cartesian = AnyChart.line();
        set1 = Set.instantiate();
        cartesian.animation(true);
        cartesian.xScroller(true).container();
        OrdinalZoom xZoom = cartesian.xZoom();
        xZoom.setToPointsCount(1500, false, null);
        xZoom.getStartRatio();
        cartesian.xAxis(0).title("Time");
        cartesian.yAxis(0).title("ppm");
        cartesian.xScale("ordinal");
        cartesian.yScale("linear");
        anyChartView1.setChart(cartesian);
        //buttons setup
        Button nh3_button = findViewById(R.id.buttonNH3);
        nh3_button.isPressed();
        Button co_button = findViewById(R.id.buttonCO);
        Button no2_button = findViewById(R.id.buttonNO2);
        Button so2_button = findViewById(R.id.buttonSO2);
        Button ch4_button = findViewById(R.id.buttonCH4);
        Button dust_button = findViewById(R.id.buttonDUST);

        nh3_button.setOnClickListener(v -> processGraphs(1));
        co_button.setOnClickListener(v -> processGraphs(2));
        no2_button.setOnClickListener(v -> processGraphs(3));
        so2_button.setOnClickListener(v -> processGraphs(4));
        ch4_button.setOnClickListener(v -> processGraphs(5));
        dust_button.setOnClickListener(v -> processGraphs(6));

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00+00:00");
        apolloClient = ApolloClient.builder()
                .serverUrl("http://api.naqm.ml:8080/v1/graphql")
                .build();
        Calendar c = Calendar.getInstance();
        Date d1 = c.getTime();
        c.add(Calendar.DATE, 1);
        Date d2 = c.getTime();
        String from = df.format(d1);
        String to = df.format(d2);
        fetchData(from, to);

        //date picker setup
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String queryDate = sdf.format(myCalendar.getTime());
        edittext= findViewById(R.id.dateText);
        edittext.setText(queryDate);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            try {
                updateLabel();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };
        edittext.setOnClickListener(v -> new DatePickerDialog(DateGraphsActivity.this, R.style.DialogTheme ,date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void fetchData(String from, String to){
        DateFilteredReadingQuery myQuery = new DateFilteredReadingQuery(from, to);
        Log.e("QUery", String.valueOf(myQuery.variables().valueMap()));
        apolloClient.query(myQuery)
                .enqueue(new ApolloCall.Callback<DateFilteredReadingQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DateFilteredReadingQuery.Data> response) {
                        Log.e("Apollo", String.valueOf(response.getData()));
                        DateFilteredReadingQuery.Data abc = response.getData();
                        List<DateFilteredReadingQuery.Naqm_AirReading> objects = abc.naqm_AirReading();
                        Log.e("Apollo", "size " + objects.size());
                        parseData(objects);
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("Apollo", "Error", e);
                    }

                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateLabel() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date dt = myCalendar.getTime();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00+00:00"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(dt);
        Date start1 = df.parse(nowAsISO);
        Log.e("Url", nowAsISO+ "  +++ ++++ +++ "+ start1);
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        Date dt2 = c.getTime();
        String somee = df.format(dt2);
        Log.e("Url", somee);
        edittext.setText(sdf.format(dt));
        fetchData(nowAsISO, somee);
    }

    public void parseData(List<DateFilteredReadingQuery.Naqm_AirReading> myobj) {
        s = myobj.size();
        Log.e("parese", "blaaa " + s);
        if(s==0){
            d.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DateGraphsActivity.this, "No data available!", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }
        date.clear();
        time.clear();
        nh3.clear();
        co.clear();
        no2.clear();
        so2.clear();
        dust.clear();
        ch4.clear();
        temperature.clear();
        humidity.clear();

        seriesDataNh3.clear();
        seriesDataCo.clear();
        seriesDataNo2.clear();
        seriesDataSo2.clear();
        seriesDataCh4.clear();
        seriesDataDust.clear();

        for (int i = 0; i< s; i++){
            DateFilteredReadingQuery.Naqm_AirReading temp = myobj.get(i);
            nh3.add(((BigDecimal) temp.nh3).doubleValue());
            co.add(((BigDecimal) temp.co).doubleValue());
            no2.add(((BigDecimal) temp.no2).doubleValue());
            so2.add(((BigDecimal) temp.so2).intValue());
            dust.add(((BigDecimal) temp.dust).intValue());
            ch4.add(((BigDecimal) temp.ch4).doubleValue());

            String t = (String) temp.timestamp;
            List<String> dateAndTime = Arrays.asList(t.split("T"));
            String mydate = dateAndTime.get(0);
            String timeTemp = dateAndTime.get(1);
            String mytime;
            if(timeTemp.contains(".")){
                List<String> timeTime = Arrays.asList(timeTemp.split("\\."));
                mytime = timeTime.get(0);
            }
            else{
                mytime = timeTemp;
            }

            date.add(mydate);
            time.add(mytime);


            seriesDataNh3.add(new ValueDataEntry(mytime, nh3.get(i)));
            seriesDataCo.add(new ValueDataEntry(mytime, co.get(i)));
            seriesDataNo2.add(new ValueDataEntry(mytime, no2.get(i)));
            seriesDataSo2.add(new ValueDataEntry(mytime, so2.get(i)));
            seriesDataCh4.add(new ValueDataEntry(mytime, ch4.get(i)));
            seriesDataDust.add(new ValueDataEntry(mytime,dust.get(i)));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processGraphs(1);
            }
        });
    }

    public void processGraphs(int choice){
        Log.e("Process", "Lists made ");
        if(s==0){
            Toast.makeText(DateGraphsActivity.this, "No data available!", Toast.LENGTH_SHORT).show();
        }
        if(choice==1){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Ammonia");
            cartesian.yScale().ticks().interval(0.01d);
            set1.data(seriesDataNh3);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Orange");
            series1.name("NH3");
        }
        else if(choice==2){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Carbon Monoxide");
            set1.data(seriesDataCo);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Brown");
            series1.name("CO");
        }
        else if(choice==3){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Nitrogen Dioxide");
            set1.data(seriesDataNo2);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Pink");
            series1.name("NO2");
        }
        else if(choice==4){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Carbon Dioxide");
            set1.data(seriesDataSo2);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Grey");
            series1.name("CO2");
        }
        else if(choice==5){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Methane");
            set1.data(seriesDataCh4);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Purple");
            series1.name("CH4");
        }
        else if(choice==6){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Dust Particles");
            set1.data(seriesDataDust);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Black");
            series1.name("Dust");
        }
        d.dismiss();
    }
}