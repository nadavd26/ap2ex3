package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ListView;

import com.example.myapplication.adapters.MessageAdapter;
import com.example.myapplication.api.API;
import com.example.myapplication.elements.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
}