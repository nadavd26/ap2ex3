package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.myapplication.api.API;
import com.example.myapplication.api.User;
import com.google.firebase.iid.FirebaseInstanceId;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private API api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        api = API.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        AppCompatButton loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            EditText usernameEt = findViewById(R.id.login_username_edittext);
            String username = usernameEt.getText().toString();
            EditText passwordEt = findViewById(R.id.login_password_edittext);
            String password = passwordEt.getText().toString();
            Intent intent = new Intent(this, ContactList.class);
            Callback<ResponseBody> tokenCallback = new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 401 || response.code() == 404) {
                            builder.setTitle("Login Error");
                            builder.setMessage("User not registered");
                            builder.show();
                            return;
                        }
                        String token = response.body().string();
                        Callback<User> userCallback = new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                String displayName = response.body().getDisplayName();
                                String profilePic = response.body().getProfilePic();
                                intent.putExtra("username", username);
                                intent.putExtra("token", token);
                                intent.putExtra("displayName", displayName);
                                intent.putExtra("profilePic", profilePic);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                int a = 1;
                            }

                        };
                        api.getUsernameInfo(token, username, userCallback);
                    } catch (Exception e) {}
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    int a = 1;
                }
            };

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Login.this, instanceIdResult -> {
                final String newToken = instanceIdResult.getToken();
                api.getToken(username, password, newToken, tokenCallback);
            });
        });

        AppCompatButton linkToRegister = findViewById(R.id.login_link_to_register);
        linkToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });

        ImageButton settings = findViewById(R.id.login_settings);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        });

    }
}