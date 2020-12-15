package com.example.naqm;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class RealtimeGraphsActivity extends BaseActivity implements AsyncFetch.onResponse {
    public List<Air> dataFromDb = new ArrayList<>();
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

    List<DataEntry> seriesDataNh3 = new ArrayList<>();
    List<DataEntry> seriesDataCo = new ArrayList<>();
    List<DataEntry> seriesDataNo2 = new ArrayList<>();
    List<DataEntry> seriesDataSo2 = new ArrayList<>();
    List<DataEntry> seriesDataCh4 = new ArrayList<>();
    List<DataEntry> seriesDataDust = new ArrayList<>();

    AnyChartView anyChartView1;
    Set set1;
    Cartesian cartesian;

    AlertDialog d;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_graphs);
        super.onCreateToolbar(getString(R.string.real_time_graphs));
        super.onCreateDrawer();
        SpotsDialog.Builder dialog = new SpotsDialog.Builder();
        dialog.setContext(this);
        dialog.setTheme(R.style.Progress_dialog);
        dialog.setCancelable(false);
        d = dialog.build();
        d.show();
        //data retrieval pre-processing
        LocalDate toDate = LocalDate.now().plusDays(1);
        LocalDate fromDate = LocalDate.now();
        String URL = "http://111.68.101.14/db/data.php?node_id=2&from=" + fromDate + "&to=" + toDate;
        Log.e("Url", URL);
        AsyncFetch database = new AsyncFetch(this);
        database.setOnResponse(this);
        database.execute(URL);

        anyChartView1 = findViewById(R.id.chartView1);
        cartesian = AnyChart.line();
        set1 = Set.instantiate();
        cartesian.animation(true);
        cartesian.xScroller(true);
        OrdinalZoom xZoom = cartesian.xZoom();
        xZoom.setToPointsCount(1500, false, null);
        xZoom.getStartRatio();
        cartesian.xAxis(0).title("Time");
        cartesian.yAxis(0).title("ppm");
        cartesian.xScale("ordinal");
        cartesian.yScale("linear");
        anyChartView1.setChart(cartesian);

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
    }
    public void onResponse(List<Air> object) {
        if(object.size()==0){
            Toast.makeText(this, "No data available!", Toast.LENGTH_SHORT).show();
            return;
        }
        Collections.reverse(object);
        dataFromDb = object;
        Log.e("Json Response assigned", "Json Response "+ object.get(0).getTime() +" lol "+ object.get(0).getDate());
        for (int i = 0; i< object.size(); i++){
            Air a = object.get(i);
            date.add(a.getDate());
            time.add(a.getTime());
            nh3.add(a.getNh3());
            co.add(a.getCo());
            no2.add(a.getNo2());
            so2.add(a.getSo2());
            dust.add(a.getDust());
            ch4.add(a.getCh4());
            temperature.add(a.getTemperature());
            humidity.add(a.getHumidity());

            seriesDataNh3.add(new ValueDataEntry(a.getTime(), a.getNh3()));
            seriesDataCo.add(new ValueDataEntry(a.getTime(), a.getCo()));
            seriesDataNo2.add(new ValueDataEntry(a.getTime(), a.getNo2()));
            seriesDataSo2.add(new ValueDataEntry(a.getTime(), a.getSo2()));
            seriesDataCh4.add(new ValueDataEntry(a.getTime(), a.getCh4()));
            seriesDataDust.add(new ValueDataEntry(a.getTime(), a.getDust()));
        }
        Log.e("Json Response assigned", "Lists made ");
        processGraphs(1);
        d.dismiss();
    }

    public void processGraphs(int choice){
        Log.e("Process", "Lists made ");
        if(dataFromDb.size()==0){
            Toast.makeText(this, "No data available!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(choice==1){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Ammonia Today");
            cartesian.yScale().ticks().interval(0.01d);
            set1.data(seriesDataNh3);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Orange");
            series1.name("NH3");
        }
        if(choice==2){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Carbon Monoxide Today");
            set1.data(seriesDataCo);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Brown");
            series1.name("CO");
        }
        if(choice==3){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Nitrogen Dioxide Today");
            set1.data(seriesDataNo2);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Pink");
            series1.name("NO2");
        }
        if(choice==4){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Carbon Dioxide Today");
            set1.data(seriesDataSo2);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Grey");
            series1.name("CO2");
        }
        if(choice==5){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Methane Today");
            set1.data(seriesDataCh4);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Purple");
            series1.name("CH4");
        }if(choice==6){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Dust Particles Today");
            set1.data(seriesDataDust);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.stroke("Black");
            series1.name("Dust");
        }
    }
}