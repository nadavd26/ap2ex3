package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.myapplication.adapters.ContactItemAdapter;
import com.example.myapplication.api.API;
import com.example.myapplication.api.ContactServer;
import com.example.myapplication.elements.ContactItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
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

    ImageView chatOwnerProfilePic;

    private  Callback<List<ContactServer>> contactsCallback;
    private List<ContactItem> convertToContactItemList(List<ContactServer> contactServers) {
        List<ContactItem> contactItems = new ArrayList<ContactItem>();
        for (ContactServer contactServer : contactServers) {
            String created = contactServer.getLastMessage() == null? "": contactServer.getLastMessage().getCreated();
            String content = contactServer.getLastMessage() == null? "": contactServer.getLastMessage().getContent();
            ContactItem contactItem = new ContactItem(contactServer.getId(),
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

        api = new API();



        contacts = new ArrayList<>();
        Intent currentIntent = getIntent();
        username = currentIntent.getStringExtra("username");
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

        contactsCallback = new Callback<List<ContactServer>>() {
            @Override
            public void onResponse(Call<List<ContactServer>> call, Response<List<ContactServer>> response) {
                contacts.clear();
                contacts.addAll(convertToContactItemList(response.body()));
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ContactServer>> call, Throwable t) {
                int a = 1;
            }
        };

        api.getContactList(token, contactsCallback);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Messages.class);
                ContactItem contactItem = contacts.get(i);
                intent.putExtra("token", token);
                intent.putExtra("id", contactItem.getChatId());
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        FloatingActionButton addChat = findViewById(R.id.contact_list_add);
        addChat.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddContact.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        api.getContactList(token, contactsCallback);
    }
}