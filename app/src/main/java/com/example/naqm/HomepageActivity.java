package com.example.naqm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import okhttp3.OkHttpClient;

public class HomepageActivity extends BaseActivity{
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

    RelativeLayout layout2;
    RelativeLayout layout4;
    RelativeLayout layout5;
    RelativeLayout layout6;

    public Dialog settingsDialog;
    AlertDialog d;

    ApolloClient myApolloClient;

    @SuppressLint("InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        super.onCreateToolbar(getString(R.string.home));
        super.onCreateDrawer();
        SpotsDialog.Builder dialog = new SpotsDialog.Builder();
        dialog.setContext(this);
        dialog.setTheme(R.style.Progress_dialog);
        dialog.setMessage("Fetching Data");
        dialog.setCancelable(false);
        d = dialog.build();
        d.show();
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

        layout2 = findViewById(R.id.carbon_monoxide);
        layout4 = findViewById(R.id.sulphur_dioxide);
        layout5 = findViewById(R.id.methane);
        layout6 = findViewById(R.id.dust);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        myApolloClient = ApolloClient.builder()
                .okHttpClient(okHttpClient)
                .serverUrl("http://api.naqm.ml:8080/v1/graphql")
                .subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory("ws://api.naqm.ml:8080/v1/graphql", okHttpClient))
                .build();
        myApolloClient.subscribe(new LatestReadingSubscription()).execute(new ApolloSubscriptionCall.Callback<LatestReadingSubscription.Data>() {
            @Override
            public void onResponse(@NotNull Response<LatestReadingSubscription.Data> response) {
                Log.e("Apollo","onResponse `${response.data()}");
                LatestReadingSubscription.Data abc = response.getData();
                LatestReadingSubscription.Naqm_AirReading myObj = abc.naqm_AirReading().get(0);
                makeObject(myObj);
                Log.e("Apollo", String.valueOf(abc));
                Log.e("Apollo", String.valueOf(myObj.timestamp));
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

                d.dismiss();
//                Toast.makeText(HomepageActivity.this, "Server failed to respond, restart application", Toast.LENGTH_SHORT).show();
                Log.e("Apollo", "Error ", e);
            }

            @Override
            public void onCompleted() {
                Log.e("Apollo", "completed");
            }

            @Override
            public void onTerminated() {
                Log.e("Apollo", "terminated");
            }

            @Override
            public void onConnected() {
                Log.e("Apollo", "connected");
            }
        });
    }

    private void makeObject(LatestReadingSubscription.Naqm_AirReading myObj){
        nh3 = ((BigDecimal) myObj.nh3).doubleValue();
        co = ((BigDecimal) myObj.co).doubleValue();
        no2 = ((BigDecimal) myObj.no2).doubleValue();
        so2 = ((BigDecimal) myObj.so2).intValue();
        dust = ((BigDecimal) myObj.dust).intValue();
        ch4 = ((BigDecimal) myObj.ch4).doubleValue();
        temperature = ((BigDecimal) myObj.temperature).intValue();
        humidity = ((BigDecimal) myObj.humidity).intValue();
        String temp = (String) myObj.timestamp;
        List<String> dateAndTime = Arrays.asList(temp.split("T"));
        date = dateAndTime.get(0);
        String timeTemp = dateAndTime.get(1);
        Log.e("Date ", date+ " ++++"+timeTemp);
        if(timeTemp.contains(".")){
            List<String> timeTime = Arrays.asList(timeTemp.split("\\."));
            time = timeTime.get(0);
            Log.e("Date ", time);
        }
        else{
            time = timeTemp;
        }
        timestamp = "Last update: "+time+" "+date;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                colorBoxes();
                setTextBoxes();
            }
        });
    }

    public void colorBoxes(){
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
        if(d.isShowing()){
            d.dismiss();
        }
    }

    public void dismissListener(View view) {
        settingsDialog.dismiss();
    }

}