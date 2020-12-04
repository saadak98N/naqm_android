package com.example.naqm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public Context getNAQMContext() {
        return getBaseContext();
    }
}