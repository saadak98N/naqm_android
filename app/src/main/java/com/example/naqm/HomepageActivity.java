package com.example.naqm;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.List;

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

    public TextView nh3_text;
    public TextView co_text;
    public TextView no2_text;
    public TextView so2_text;
    public TextView dust_text;
    public TextView ch4_text;
    public TextView timestamp_text;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        super.onCreateToolbar(getString(R.string.home));
        super.onCreateDrawer();
        nh3_text = findViewById(R.id.ammonia_reading);
        co_text = findViewById(R.id.carbon_monoxide_reading);
        no2_text = findViewById(R.id.nitrogen_dioxide_reading);
        so2_text = findViewById(R.id.sulphur_dioxide_reading);
        dust_text = findViewById(R.id.dust_reading);
        ch4_text = findViewById(R.id.methane_reading);
        timestamp_text = findViewById(R.id.update_date);

        LocalDate toDate = LocalDate.now().plusDays(1);
        LocalDate fromDate = LocalDate.now();
        String URL = "http://111.68.101.14/db/data.php?node_id=2&from=" + fromDate + "&to=" + toDate;
        Log.e("Url", URL);
        AsyncFetch database = new AsyncFetch(this);
        database.setOnResponse(this);
        database.execute(URL);
    }
    public void onResponse(List<Air> object) {
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
    }

    public void colorBoxes(){
        RelativeLayout layout1 =(RelativeLayout)findViewById(R.id.ammonia);
        RelativeLayout layout2 =(RelativeLayout)findViewById(R.id.carbon_monoxide);
        RelativeLayout layout3 =(RelativeLayout)findViewById(R.id.nitrogen_dioxide);
        RelativeLayout layout4 =(RelativeLayout)findViewById(R.id.sulphur_dioxide);
        RelativeLayout layout5 =(RelativeLayout)findViewById(R.id.methane);
        RelativeLayout layout6 =(RelativeLayout)findViewById(R.id.dust);

        if(nh3 < 200.0){
            layout1.setBackgroundResource(R.drawable.green_rect);
        }else if (nh3 <800.0){
            layout1.setBackgroundResource(R.drawable.yellow_rect);
        }else{
            layout1.setBackgroundResource(R.drawable.red_rect);
        }

        if(co < 4.4){
            layout2.setBackgroundResource(R.drawable.green_rect);
        }else if (co <12.4){
            layout2.setBackgroundResource(R.drawable.yellow_rect);
        }else{
            layout2.setBackgroundResource(R.drawable.red_rect);
        }

        if(no2 < 0.053){
            layout3.setBackgroundResource(R.drawable.green_rect);
        }else if (no2 <0.36){
            layout3.setBackgroundResource(R.drawable.yellow_rect);
        }else{
            layout3.setBackgroundResource(R.drawable.red_rect);
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
        nh3_text.setText(Double.toString(nh3));
        co_text.setText(Double.toString(co));
        no2_text.setText(Double.toString(no2));
        so2_text.setText(Integer.toString(so2));
        ch4_text.setText(Double.toString(ch4));
        dust_text.setText(Integer.toString(dust));
        timestamp_text.setText(timestamp);
    }
}