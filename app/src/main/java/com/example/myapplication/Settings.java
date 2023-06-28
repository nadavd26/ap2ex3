package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import com.example.myapplication.api.API;

public class Settings extends AppCompatActivity {
    private AppCompatButton modeButton;
    private EditText ipEt;
    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        api = API.getInstance();
        AppCompatButton submitButton = findViewById(R.id.settings_submit_button);
        modeButton = findViewById(R.id.settings_mode_button);
        ipEt = findViewById(R.id.settings_ip);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        String baseUrl = sharedPreferences.getString("serverIP", "http://10.0.2.2:5000/api/");
        final String[] mode = {sharedPreferences.getString("mode", "LIGHT")};
        ipEt.setText(baseUrl);
        submitButton.setOnClickListener(view -> {

            String updatedServerUrl = ipEt.getText().toString();
            editor.putString("serverIP", updatedServerUrl);
            editor.putString("mode", mode[0]);
            editor.apply();
            api.setBaseUrl(updatedServerUrl);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        if (mode[0].equals("LIGHT")) {
            modeButton.setText(R.string.light_mode);
        } else {
            modeButton.setText(R.string.dark_mode);
        }
        modeButton.setOnClickListener(view -> {
            if (mode[0].equals("LIGHT")) {
                mode[0] = "DARK";
                modeButton.setText(R.string.dark_mode);
            } else {
                mode[0] = "LIGHT";
                modeButton.setText(R.string.light_mode);
           }
            editor.apply();
        });
    }
}