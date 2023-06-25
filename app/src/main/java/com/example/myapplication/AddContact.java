package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;

import com.example.myapplication.api.API;
import com.example.myapplication.api.AddChat;
import com.example.myapplication.elements.ContactItem;
import com.example.myapplication.room.AppDB;
import com.example.myapplication.room.ContactDao;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContact extends AppCompatActivity {
    private API api;
    private AppDB db;
    private ContactDao contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        api = API.getInstance();
        String owner = getIntent().getStringExtra("owner");
        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, owner)
                .build();
        contactDao = db.contactDao();
        String token = getIntent().getStringExtra("token");
        Button save = findViewById(R.id.add_contact_button);
        save.setOnClickListener(view -> {
            EditText usernameText = findViewById(R.id.add_contact_username);
            String username = usernameText.getText().toString();
            Callback<AddChat> addChatCallback = new Callback<AddChat>() {
                @Override
                public void onResponse(Call<AddChat> call, Response<AddChat> response) {
                    if (response.code() == 200) {
                        AddChat addChat = response.body();
                        String displayName = addChat.getUser().getDisplayName();
                        String profilePic = addChat.getUser().getProfilePic();
                        try {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        contactDao.insert(new ContactItem(Integer.parseInt(addChat.getId()),
                                                "",
                                                "",
                                                displayName,
                                                profilePic));
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        } catch (Exception e) {}
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddContact.this);
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setTitle("Login Error");
                        builder.setMessage("User not registered");
                        builder.show();
                    }
                }

                @Override
                public void onFailure(Call<AddChat> call, Throwable t) {
                }
            };
            api.addChat(token, username, addChatCallback);
        });
    }
}