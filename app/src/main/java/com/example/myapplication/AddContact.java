package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;

import com.example.myapplication.api.API;
import com.example.myapplication.api.AddChat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContact extends AppCompatActivity {
    private API api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        api = new API();
        String token = getIntent().getStringExtra("token");
        Button save = findViewById(R.id.add_contact_button);
        save.setOnClickListener(view -> {
            EditText usernameText = findViewById(R.id.add_contact_username);
            String username = usernameText.getText().toString();
            Callback<AddChat> addChatCallback = new Callback<AddChat>() {
                @Override
                public void onResponse(Call<AddChat> call, Response<AddChat> response) {
                    if (response.code() == 200) {
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddContact.this);
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setTitle("Login Error");
                        builder.setMessage("User not registered");
                        builder.show();
                    }
                }

                @Override
                public void onFailure(Call<AddChat> call, Throwable t) {}
            };
            api.addChat(token, username, addChatCallback);
        });
    }
}