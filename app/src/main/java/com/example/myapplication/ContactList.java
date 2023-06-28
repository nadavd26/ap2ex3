package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.myapplication.adapters.ContactItemAdapter;
import com.example.myapplication.api.API;
import com.example.myapplication.api.ContactServer;
import com.example.myapplication.elements.ContactItem;
import com.example.myapplication.room.AppDB;
import com.example.myapplication.room.ContactDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactList extends AppCompatActivity {
    private List<ContactItem> contacts;
    private ContactItemAdapter contactAdapter;
    private ListView contactList;
    private String username;
    private String displayName;
    private String token;
    private String profilePic;
    private API api;

    private AppDB db;

    private FireBase fireBase;

    private ContactDao contactDao;

    private boolean isOn = false;
    ImageView chatOwnerProfilePic;

    TextView chatOwnerDisplayName;

    ImageButton settings;

    private  Callback<List<ContactServer>> contactsCallback;

    private String goodLookingDate(String serverDate) {
        String date1 = serverDate.substring(0, 10);
        String date2 = serverDate.substring(11, 13);
        Integer i = Integer.parseInt(date2);
        i = (i + 3) % 24;
        String date3 = serverDate.substring(13, 16);
        return  date1 + " " + i + date3 ;
    }
    private List<ContactItem> convertToContactItemList(List<ContactServer> contactServers) {
        List<ContactItem> contactItems = new ArrayList<ContactItem>();
        for (ContactServer contactServer : contactServers) {
            String created = contactServer.getLastMessage() == null? "": goodLookingDate(contactServer.getLastMessage().getCreated());
            String content = contactServer.getLastMessage() == null? "": contactServer.getLastMessage().getContent();
            ContactItem contactItem = new ContactItem(Integer.parseInt(contactServer.getId()),
                    created,
                    content,
                    contactServer.getUser().getDisplayName(),
                    contactServer.getUser().getProfilePic());
            contactItems.add(contactItem);
        }

        return contactItems;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        api = API.getInstance();
        contacts = new ArrayList<>();
        Intent currentIntent = getIntent();
        username = currentIntent.getStringExtra("username");
        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, username)
                .build();
        contactDao = db.contactDao();
        settings = findViewById(R.id.contact_list_settings);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        });
        displayName = currentIntent.getStringExtra("displayName");
        token = currentIntent.getStringExtra("token");
        profilePic = currentIntent.getStringExtra("profilePic");
        contactAdapter = new ContactItemAdapter(contacts);
        contactList = findViewById(R.id.contact_list);
        contactList.setAdapter(contactAdapter);
        chatOwnerProfilePic = findViewById(R.id.contact_list_chat_owner_image);
        byte[] decodedString = Base64.decode(profilePic, Base64.DEFAULT);
        Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        chatOwnerProfilePic.setImageBitmap(profilePicBitmap);
        chatOwnerDisplayName = findViewById(R.id.contact_item_chat_owner_display_name);
        chatOwnerDisplayName.setText(displayName);
        ImageButton logout = findViewById(R.id.logout);
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isOn", true);
        editor.apply();
        logout.setOnClickListener( l -> {
            finish();
        });

        fireBase = FireBase.getInstance();
        fireBase.setContactDao(contactDao);
        fireBase.setContactItemAdapter(contactAdapter);
        fireBase.setMessageDao(db.messageDao());
        fireBase.setContacts(contacts);
        fireBase.setUsername(username);
        fireBase.setInMessages(false);
        contactsCallback = new Callback<List<ContactServer>>() {
            @Override
            public void onResponse(Call<List<ContactServer>> call, Response<List<ContactServer>> response) {
                contacts.clear();
                List<ContactItem> contactItems = convertToContactItemList(response.body());
                for( ContactItem updatedContact : contactItems) {
                    updatedContact.setLastMessage(FireBase.beautifulLastMessage(updatedContact.getLastMessage()));
                }
                contacts.addAll(contactItems);
                contactAdapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //update contact dao to match server
                        contactDao.deleteAll();
                        for (ContactItem contactItem: contactItems) {
                            contactDao.insert(contactItem);
                        }
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<List<ContactServer>> call, Throwable t) {
                int a = 1;
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ContactItem> updatedContacts = contactDao.index();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contacts.clear();
                        for( ContactItem updatedContact : updatedContacts) {
                            updatedContact.setLastMessage(FireBase.beautifulLastMessage(updatedContact.getLastMessage()));
                        }
                        contacts.addAll(updatedContacts);
                        contactAdapter.notifyDataSetChanged();
                    }
                });

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {}
                api.getContactList(token, contactsCallback);
            }
        }).start();

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Messages.class);
                ContactItem contactItem = contacts.get(i);
                intent.putExtra("token", token);
                intent.putExtra("chatId", contactItem.getId());
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        FloatingActionButton addChat = findViewById(R.id.contact_list_add);
        addChat.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddContact.class);
            intent.putExtra("token", token);
            intent.putExtra("owner", username);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fireBase.setInMessages(false);
        // Define a Handler instance
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ContactItem> updatedContacts = contactDao.index();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contacts.clear();
                        for( ContactItem updatedContact : updatedContacts) {
                            updatedContact.setLastMessage(FireBase.beautifulLastMessage(updatedContact.getLastMessage()));
                        }
                        contacts.addAll(updatedContacts);
                        contactAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}