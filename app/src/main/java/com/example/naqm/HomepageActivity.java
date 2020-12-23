package com.example.naqm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomepageActivity extends BaseActivity implements AsyncFetch.onResponse{
    public String date;
    public String time;
    public Double nh3;
    public Double co;
    public Double no2;
    public int so2;
    public int dust;
    public Double ch4;
    public int temperature;
    public int humidity;
    public String timestamp;

    public TextView temp_text;
    public TextView co_text;
    public TextView humidity_text;
    public TextView so2_text;
    public TextView dust_text;
    public TextView ch4_text;
    public TextView timestamp_text;

    public Dialog settingsDialog;

    ScheduledExecutorService scheduleTaskExecutor;
    @SuppressLint("InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        super.onCreateToolbar(getString(R.string.home));
        super.onCreateDrawer();
        temp_text = findViewById(R.id.temperature_reading);
        co_text = findViewById(R.id.carbon_monoxide_reading);
        humidity_text = findViewById(R.id.humidity_reading);
        so2_text = findViewById(R.id.sulphur_dioxide_reading);
        dust_text = findViewById(R.id.dust_reading);
        ch4_text = findViewById(R.id.methane_reading);
        timestamp_text = findViewById(R.id.update_date);

        Button help_button = findViewById(R.id.help_button);
        help_button.setOnClickListener(v -> {
            settingsDialog = new Dialog(this);
            Objects.requireNonNull(settingsDialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_range_table, null));
            settingsDialog.setCancelable(false);
            settingsDialog.setCanceledOnTouchOutside(false);
            settingsDialog.show();
        });

        LocalDate toDate = LocalDate.now().plusDays(1);
        LocalDate fromDate = LocalDate.now();
        String URL = "http://111.68.101.14/db/data.php?node_id=2&from=" + fromDate + "&to=" + toDate;
        Log.e("Url", URL);
        AsyncFetch database = new AsyncFetch(this);
        database.setOnResponse(this);
        database.execute(URL);
    }
    public void onResponse(List<Air> object) {
        if(object.size()==0){
            Toast.makeText(this, "No data available!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("Json Response assigned", "Json Response "+ object.get(0).getTime() +" lol "+ object.get(0).getDate());
        Air a = object.get(0);
        date = a.getDate();
        time = a.getTime();
        nh3 = a.getNh3();
        co = a.getCo();
        no2 = a.getNo2();
        so2 = a.getSo2();
        dust = a.getDust();
        ch4 = a.getCh4();
        temperature = a.getTemperature();
        humidity = a.getHumidity();
        timestamp = "Last update: "+time + " "+ date;
        Log.e("Json Response assigned", "Lists made ");

        colorBoxes();
        setTextBoxes();
        startRealtime();
    }

    public void colorBoxes(){
        RelativeLayout layout2 = findViewById(R.id.carbon_monoxide);
        RelativeLayout layout4 = findViewById(R.id.sulphur_dioxide);
        RelativeLayout layout5 = findViewById(R.id.methane);
        RelativeLayout layout6 = findViewById(R.id.dust);

        if(co < 4.4){
            layout2.setBackgroundResource(R.drawable.green_rect);
        }else if (co <12.4){
            layout2.setBackgroundResource(R.drawable.yellow_rect);
        }else{
            layout2.setBackgroundResource(R.drawable.red_rect);
        }

        if(so2 < 1000){
            layout4.setBackgroundResource(R.drawable.green_rect);
        }else if (so2 <5000){
            layout4.setBackgroundResource(R.drawable.yellow_rect);
        }else{
            layout4.setBackgroundResource(R.drawable.red_rect);
        }

        if(ch4 < 50){
            layout5.setBackgroundResource(R.drawable.green_rect);
        }else if (ch4 <150){
            layout5.setBackgroundResource(R.drawable.yellow_rect);
        }else{
            layout5.setBackgroundResource(R.drawable.red_rect);
        }

        if(dust < 12){
            layout6.setBackgroundResource(R.drawable.green_rect);
        }else if (dust <55.4){
            layout6.setBackgroundResource(R.drawable.yellow_rect);
        }else{
            layout6.setBackgroundResource(R.drawable.red_rect);
        }
    }

    @SuppressLint("SetTextI18n")
    public void setTextBoxes(){
        temp_text.setText(Integer.toString(temperature));
        co_text.setText(Double.toString(co));
        humidity_text.setText(Integer.toString(humidity));
        so2_text.setText(Integer.toString(so2));
        ch4_text.setText(Double.toString(ch4));
        dust_text.setText(Integer.toString(dust));
        timestamp_text.setText(timestamp);
    }

    public void dismissListener(View view) {
        settingsDialog.dismiss();
    }

    public void startRealtime(){
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        super.setService(scheduleTaskExecutor);
        // This schedule a runnable task every 5 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                getData5minutes();
            }
        }, 5*60, 5*60, TimeUnit.SECONDS);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getData5minutes(){
        LocalDate toDate = LocalDate.now().plusDays(1);
        LocalDate fromDate = LocalDate.now();
        Log.e("Json Response BK", "running bk ");
        String URL = "http://111.68.101.14/db/data.php?node_id=2&from=" + fromDate + "&to=" + toDate;
        Log.e("Url", URL);
        AsyncFetch database = new AsyncFetch(this);
        database.setOnResponse(this);
        database.execute(URL);
    }
}