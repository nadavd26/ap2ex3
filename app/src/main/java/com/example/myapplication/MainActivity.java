package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.ListView;

import com.example.myapplication.adapters.MessageAdapter;
import com.example.myapplication.api.API;
import com.example.myapplication.elements.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static Context context;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", "LIGHT");
        if (mode.equals("LIGHT")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        context = this;
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int a;
    }
}