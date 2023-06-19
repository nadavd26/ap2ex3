package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ListView;

import com.example.myapplication.adapters.MessageAdapter;
import com.example.myapplication.api.API;
import com.example.myapplication.api.MessageServer;
import com.example.myapplication.elements.Message;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Messages extends AppCompatActivity {
    private API api;
    private ListView messagesList;
    private MessageAdapter messageAdapter;

    private List<Message> serverMessagesToMessages(List<MessageServer> messageServerList, String username) {
        List<Message> messageList = new ArrayList<>();
        for (int i = messageServerList.size() - 1; i >= 0; i--) {
            MessageServer messageServer = messageServerList.get(i);
            boolean me = messageServer.getSender().getUsername().equals(username);
            Message message = new Message(messageServer.getCreated(), messageServer.getContent(), me);
            messageList.add(message);
        }
        return messageList;
    }

    private void scrollDown() {
        messagesList.postDelayed(new Runnable() {
            @Override
            public void run() {
                messagesList.setSelection(messageAdapter.getCount() - 1);
            }
        }, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        api = new API();
        String id = getIntent().getStringExtra("id");
        String token = getIntent().getStringExtra("token");
        String username = getIntent().getStringExtra("username");
        messagesList = findViewById(R.id.messages_list);
        List<Message> messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        messagesList.setAdapter(messageAdapter);
        scrollDown();
        Callback<List<MessageServer>> messagesServerCallback = new Callback<List<MessageServer>>() {
            @Override
            public void onResponse(Call<List<MessageServer>> call, Response<List<MessageServer>> response) {
                messages.addAll(serverMessagesToMessages(response.body(), username));
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<MessageServer>> call, Throwable t) {
                int a = 1;
            }
        };

        api.getMessages(token, id, messagesServerCallback);
        AppCompatButton sendButton = findViewById(R.id.messages_button_send);
        sendButton.setOnClickListener(view -> {
            EditText et = findViewById(R.id.edit_text_message);
            if (et.getText().toString().equals("")) {
                return;
            }
            Callback<MessageServer> sendMessageCallback = new Callback<MessageServer>() {
                @Override
                public void onResponse(Call<MessageServer> call, Response<MessageServer> response) {
                    Message message = new Message(response.body().getCreated(),
                            response.body().getContent(),
                            true);
                    messages.add(message);
                    messageAdapter.notifyDataSetChanged();
                    scrollDown();

                }

                @Override
                public void onFailure(Call<MessageServer> call, Throwable t) {}
            };
            api.sendMessage(token, id, et.getText().toString(), sendMessageCallback);
            et.setText("");
        });

            AppCompatButton logoutButton = findViewById(R.id.messages_button_back_to_contact_list);
            logoutButton.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollDown();
    }
}