package com.example.naqm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RealtimeGraphsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AsyncFetch.onResponse {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    public List<Air> dataFromDb;
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

    private Button nh3_button;
    private Button co_button;
    private Button no2_button;
    private Button so2_button;
    private Button dust_button;
    private Button ch4_button;

    List<DataEntry> seriesDataNh3 = new ArrayList<>();
    List<DataEntry> seriesDataCo = new ArrayList<>();
    List<DataEntry> seriesDataNo2 = new ArrayList<>();
    List<DataEntry> seriesDataSo2 = new ArrayList<>();
    List<DataEntry> seriesDataCh4 = new ArrayList<>();
    List<DataEntry> seriesDataDust = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_graphs);

        //Setting the action bar
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        TextView title = findViewById(R.id.title_text);
        title.setText(R.string.real_time_graphs);
        setSupportActionBar(toolbar);

        //setting the sidebar
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //data retrieval pre-processing
        LocalDate toDate = LocalDate.now().plusDays(1);
        LocalDate fromDate = LocalDate.now();
        String URL = "http://111.68.101.14/db/data.php?node_id=2&from=" + fromDate + "&to=" + toDate;
        Log.e("Url", URL);
        AsyncFetch database = new AsyncFetch(this);
        database.setOnResponse(this);
        database.execute(URL);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_item_one) {
            Toast.makeText(this, "Clicked item one", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(RealtimeGraphsActivity.this, HomepageActivity.class);
            RealtimeGraphsActivity.this.startActivity(myIntent);
        }
        if (id == R.id.nav_item_two) {
            Toast.makeText(this, "Clicked item two", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.nav_item_three) {
            Toast.makeText(this, "Clicked item three", Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void onResponse(List<Air> object) {
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
    }

    public void processGraphs(int choice){
        Log.e("Json Response assigned", "Making chart");

        AnyChartView anyChartView = null;
        if (choice==1){
            Log.e("Json Response assigned", "1st");
            anyChartView = findViewById(R.id.chartView1);
            APIlib.getInstance().setActiveAnyChartView(anyChartView);
        }
        Cartesian cartesian = AnyChart.line();
        cartesian.animation(true);
        if(choice==1){
            Log.e("Json Response assigned", "2");
            cartesian.title("Trend of Ammonia Today");
        }
        cartesian.xScroller(true);
        OrdinalZoom xZoom = cartesian.xZoom();
        xZoom.setToPointsCount(50, false, null);
        xZoom.getStartRatio();
        xZoom.getEndRatio();

        if(choice==1){
            Log.e("Json Response assigned", "3rd");
            Line series1 = cartesian.line(seriesDataNh3);
            series1.name("NH3");
        }
        cartesian.xAxis(0).title("Time");
        cartesian.yAxis(0).title("ppm");
        assert anyChartView != null;
        anyChartView.setChart(cartesian);
    }
}