package com.example.naqm;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DateGraphsActivity extends BaseActivity implements AsyncFetch.onResponse {
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

    EditText edittext;
    final Calendar myCalendar = Calendar.getInstance();
    String URL;
    String URL2 = "nothing";

    AnyChartView anyChartView1;
    Set set1;
    Cartesian cartesian;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_graphs);
        super.onCreateToolbar(getString(R.string.date_wise_graphs));
        super.onCreateDrawer();
        //data retrieval pre-processing
        LocalDate toDate = LocalDate.now().plusDays(1);
        LocalDate fromDate = LocalDate.now();
        URL = "http://111.68.101.14/db/data.php?node_id=2&from=" + fromDate + "&to=" + toDate;
        Log.e("Url", URL);
        AsyncFetch database = new AsyncFetch(this);
        database.setOnResponse(this);
        database.execute(URL);
        //chart setup
        anyChartView1 = findViewById(R.id.chartView1);
        cartesian = AnyChart.line();
        set1 = Set.instantiate();
        cartesian.animation(true);
        cartesian.xScroller(true);
        OrdinalZoom xZoom = cartesian.xZoom();
        xZoom.setToPointsCount(50, false, null);
        xZoom.getStartRatio();
        cartesian.xAxis(0).title("Time");
        cartesian.yAxis(0).title("ppm");
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
            updateLabel();
        };
        edittext.setOnClickListener(v -> new DatePickerDialog(DateGraphsActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String queryDate = sdf.format(myCalendar.getTime());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fromDate = LocalDate.parse(queryDate, dtf);
        LocalDate toDate = fromDate.plusDays(1);
        edittext.setText(sdf.format(myCalendar.getTime()));
        URL2 = "http://111.68.101.14/db/data.php?node_id=2&from=" + fromDate + "&to=" + toDate;
        Log.e("Url", URL2);
        AsyncFetch database = new AsyncFetch(this);
        database.setOnResponse(this);
        database.execute(URL2);
    }

    public void onResponse(List<Air> object) {
        Collections.reverse(object);
        dataFromDb.clear();
        dataFromDb = object;
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
    }

    public void processGraphs(int choice){
        Log.e("Process", "Lists made ");

        if(choice==1){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Ammonia");
            set1.data(seriesDataNh3);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.name("NH3");
        }
        if(choice==2){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Carbon Monoxide");
            set1.data(seriesDataCo);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.name("CO");
        }
        if(choice==3){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Nitrogen Dioxide");
            set1.data(seriesDataNo2);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.name("NO2");
        }
        if(choice==4){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Carbon Dioxide");
            set1.data(seriesDataSo2);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.name("CO2");
        }
        if(choice==5){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Methane");
            set1.data(seriesDataCh4);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.name("CH4");
        }if(choice==6){
            APIlib.getInstance().setActiveAnyChartView(anyChartView1);
            cartesian.title("Trend of Dust Particles");
            set1.data(seriesDataDust);
            Line series1;
            Mapping series1Data = set1.mapAs("{ x: 'x', value: 'value' }");
            series1 = cartesian.line(series1Data);
            series1.name("Dust");
        }
    }
}