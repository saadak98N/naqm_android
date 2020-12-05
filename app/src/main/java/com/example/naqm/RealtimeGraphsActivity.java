package com.example.naqm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class RealtimeGraphsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AsyncFetch.onResponse {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate date = LocalDate.now().plusDays(1);
        String toDate = sdf.format(date);
        LocalDate date1 = LocalDate.now().minusDays(2);
        String fromDate = sdf.format(date1);
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
    public void onResponse(JSONObject object) {
        Log.e("Json Response", "Json Response");
    }
}