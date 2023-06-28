package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.widget.EditText;
import android.widget.ListView;

import com.example.myapplication.adapters.MessageAdapter;
import com.example.myapplication.api.API;
import com.example.myapplication.api.MessageServer;
import com.example.myapplication.elements.ContactItem;
import com.example.myapplication.elements.Message;
import com.example.myapplication.room.AppDB;
import com.example.myapplication.room.ContactDao;
import com.example.myapplication.room.MessageDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Messages extends AppCompatActivity {
    private API api;
    private ListView messagesList;
    private MessageAdapter messageAdapter;

    private int chatId;

    private ContactDao contactDao;

    private MessageDao messageDao;

    private String goodLookingDate(String serverDate) {
        String date1 = serverDate.substring(0, 10);
        String date2 = serverDate.substring(11, 13);
        int i = Integer.parseInt(date2);
        i = (i + 3) % 24;
        String date3 = serverDate.substring(13, 16);
        return  date1 + " " + i + date3 ;
    }

    private List<Message> serverMessagesToMessages(List<MessageServer> messageServerList, String username) {
        List<Message> messageList = new ArrayList<>();
        for (int i = messageServerList.size() - 1; i >= 0; i--) {
            MessageServer messageServer = messageServerList.get(i);
            boolean me = messageServer.getSender().getUsername().equals(username);
            Message message = new Message(goodLookingDate(messageServer.getCreated()),
                    chatId,
                    messageServer.getContent(),
                    me);
            messageList.add(message);
        }
        return messageList;
    }

    private void scrollDown() {
        messagesList.postDelayed(() -> messagesList.setSelection(messageAdapter.getCount() - 1), 100);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        api = API.getInstance();
        chatId = getIntent().getIntExtra("chatId", 0);
        String token = getIntent().getStringExtra("token");
        String username = getIntent().getStringExtra("username");
        messagesList = findViewById(R.id.messages_list);
        List<Message> messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        messagesList.setAdapter(messageAdapter);
        scrollDown();
        AppDB db = Room.databaseBuilder(getApplicationContext(), AppDB.class, username)
                .build();
        messageDao = db.messageDao();
        contactDao = db.contactDao();
        FireBase fireBase = FireBase.getInstance();
        fireBase.setContactDao(contactDao);
        fireBase.setMessageAdapter(messageAdapter);
        fireBase.setMessageDao(db.messageDao());
        fireBase.setMessages(messages);
        fireBase.setMessagesList(messagesList);
        fireBase.setChatID(chatId);
        fireBase.setInMessages(true);
        Callback<List<MessageServer>> messagesServerCallback = new Callback<List<MessageServer>>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageServer>> call, Response<List<MessageServer>> response) {
                messages.clear();
                assert response.body() != null;
                List<Message> messageList = serverMessagesToMessages(response.body(), username);
                messages.addAll(messageList);
                messageAdapter.notifyDataSetChanged();
                new Thread(() -> {
                    List<Message> daoIndex = messageDao.index();
                    for (Message message:daoIndex) {
                        if (message.getChatId() == chatId) {
                                messageDao.delete(message);
                        }
                    }

                    for (Message message: messageList) {
                        messageDao.insert(message);
                    }
                }).start();
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageServer>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        };
        new Thread(() -> {
            List<Message> updatedMessages = messageDao.getMessageList(chatId);
            runOnUiThread(() -> {
                messages.clear();
                messages.addAll(updatedMessages);
                messageAdapter.notifyDataSetChanged();
            });

            try {
                Thread.sleep(2000);
            } catch (Exception e) {e.printStackTrace();}
            api.getMessages(token, String.valueOf(chatId), messagesServerCallback);
        }).start();
        AppCompatButton sendButton = findViewById(R.id.messages_button_send);
        sendButton.setOnClickListener(view -> {
            EditText et = findViewById(R.id.edit_text_message);
            if (et.getText().toString().equals("")) {
                return;
            }

            Callback<MessageServer> sendMessageCallback = new Callback<MessageServer>() {
                @Override
                public void onResponse(@NonNull Call<MessageServer> call, Response<MessageServer> response) {
                    assert response.body() != null;
                    Message message = new Message(goodLookingDate(response.body().getCreated()),
                            chatId,
                            response.body().getContent(),
                            true);
                    messages.add(message);
                    new Thread(() -> {
                        messageDao.insert(message);
                        List<ContactItem> currentContact = contactDao.index();
                        //find contact of chatId
                        ContactItem currentContactItem = new ContactItem();
                        for (ContactItem contactItem : currentContact) {
                            if (contactItem.getId() == chatId) {
                                currentContactItem = contactItem;
                            }
                        }
                        currentContactItem.setLastMessage(message.getContent());
                        currentContactItem.setDate(message.getDate());
                        contactDao.update(currentContactItem);
                    }).start();
                    messageAdapter.notifyDataSetChanged();
                    scrollDown();

                }

                @Override
                public void onFailure(@NonNull Call<MessageServer> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            };
            api.sendMessage(token, String.valueOf(chatId), et.getText().toString(), sendMessageCallback);
            et.setText("");
        });

            AppCompatButton logoutButton = findViewById(R.id.messages_button_back_to_contact_list);
            logoutButton.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollDown();
    }
}