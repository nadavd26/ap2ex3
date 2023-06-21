package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import com.example.myapplication.api.API;

public class Settings extends AppCompatActivity {
    private AppCompatButton submitButton;
    private AppCompatButton modeButton;
    private EditText ipEt;

    private enum Mode {
        LIGHT,
        DARK
    }

    private Mode mode;

    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        api = API.getInstance();
        submitButton = findViewById(R.id.settings_submit_button);
        modeButton = findViewById(R.id.settings_mode_button);
        ipEt = findViewById(R.id.settings_ip);
        mode = Mode.LIGHT;
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String baseUrl = sharedPreferences.getString("serverIP", "");
        ipEt.setText(baseUrl);
        submitButton.setOnClickListener(view -> {
            String updatedServerUrl = ipEt.getText().toString();
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putString("serverIP", updatedServerUrl);
            editor.apply();
            api.setBaseUrl(updatedServerUrl);
            finish();
        });

        modeButton.setOnClickListener(view -> {
            if (mode == Mode.LIGHT) {
                mode = Mode.DARK;
                modeButton.setText("Dark mode");
            } else {
                mode = Mode.LIGHT;
                modeButton.setText("Light mode");
            }
        });
    }
}