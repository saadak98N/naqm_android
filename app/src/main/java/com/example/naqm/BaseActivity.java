package com.example.naqm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    public Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = true;
    public Context getNAQMContext() {
        return getBaseContext();
    }

    protected void onCreateToolbar(String title){
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        TextView t = findViewById(R.id.title_text);
        t.setText(title);
        setSupportActionBar(toolbar);
    }

    protected void onCreateDrawer() {

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(getParent(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        NavigationView navigationView= findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)  {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_item_one){
            Intent myIntent = new Intent(this, HomepageActivity.class);
            startActivity(myIntent);
        }
        if(id==R.id.nav_item_two){
            Intent myIntent = new Intent(this, RealtimeGraphsActivity.class);
            startActivity(myIntent);
        }
        if(id==R.id.nav_item_three){
            Intent myIntent = new Intent(this, DateGraphsActivity.class);
            startActivity(myIntent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = false;
                Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
            } else {
                finishAffinity();
            }
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=true, 2000);
        }
    }
}