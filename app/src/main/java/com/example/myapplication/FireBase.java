package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;


import com.example.myapplication.adapters.ContactItemAdapter;
import com.example.myapplication.elements.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.adapters.MessageAdapter;
import com.example.myapplication.elements.ContactItem;
import com.example.myapplication.room.ContactDao;
import com.example.myapplication.room.MessageDao;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Objects;

public class FireBase extends FirebaseMessagingService {

    private static FireBase fireBase;

    private MessageAdapter messageAdapter;


    private final Handler handler = new Handler(Looper.getMainLooper());
    private ContactItemAdapter contactItemAdapter;

    private MessageDao messageDao;
    private ContactDao contactDao;

    private String username;

    private int chatID;

    private List<Message> messages;

    private List<ContactItem> contacts;

    private ListView messagesList;
    private boolean isInMessages;

    public FireBase() {
    }

    public static FireBase getInstance() {
        if (fireBase == null){
            fireBase = new FireBase();
        }
        return fireBase;
    }

    public static String beautifulLastMessage(String lastMessage){
        if(lastMessage.length() < 24)
            return lastMessage;
        return lastMessage.substring(0,20) + "...";
    }

    private void scrollDown() {
        messagesList.postDelayed(() -> messagesList.setSelection(messageAdapter.getCount() - 1), 100);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences("MyPrefs", MainActivity.context.MODE_PRIVATE);
        boolean isOn = sharedPreferences.getBoolean("isOn", false);
        if (remoteMessage.getNotification() != null && isOn && fireBase.username.equals(remoteMessage.getData().get("username"))) {
            createNotificationChannel();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.drawable.baseline_notifications_active_24)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            Message message = new Message(remoteMessage.getData().get("created"), Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("chatID"))),remoteMessage.getNotification().getBody(), false);
            new Thread(() -> {
                message.setDate(goodLookingDate(message.getDate()));
                fireBase.messageDao.insert(message);
                List<ContactItem> currentContact = fireBase.contactDao.index();
                //find contact of chatId
                ContactItem currentContactItem = new ContactItem();
                boolean found = false;
                for (ContactItem contactItem : currentContact) {
                    if (contactItem.getId() == Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("chatID")))) {
                        currentContactItem = contactItem;
                        found = true;
                    }
                }
                if(!found){
                    int chatId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("chatID")));
                    String sender = Objects.requireNonNull(remoteMessage.getNotification().getTitle()).substring(14);
                    ContactItem newContactItem = new ContactItem(chatId, message.getDate(), beautifulLastMessage(message.getContent()), sender, remoteMessage.getData().get("profilePic"));
                    fireBase.contactDao.insert(newContactItem);
                    fireBase.contacts.add(newContactItem);

                }
                else {
                    currentContactItem.setLastMessage(beautifulLastMessage(message.getContent()));
                    currentContactItem.setDate(message.getDate());
                    fireBase.contactDao.update(currentContactItem);
                }


                handler.post(() -> {
                    if (fireBase.isInMessages) {
                        if(Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("chatID"))) == fireBase.chatID) {
                            fireBase.messages.add(message);
                            fireBase.messageAdapter.notifyDataSetChanged();
                            fireBase.scrollDown();
                        }
                    } else {
                        for (ContactItem contactItem : fireBase.contacts) {
                            if (contactItem.getId() == Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("chatID")))) {
                                contactItem.setLastMessage(beautifulLastMessage(message.getContent()));
                                contactItem.setDate(message.getDate());
                            }
                        }
                        fireBase.contactItemAdapter.notifyDataSetChanged();
                    }
                });

            }).start();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManager.notify(1, builder.build());
        }


    }

    private String goodLookingDate(String date) {
        String date1 = date.substring(0, 10);
        String date2 = date.substring(11, 13);
        int i = Integer.parseInt(date2);
        i = (i + 3) % 24;
        String date3 = date.substring(13, 16);
        return  date1 + " " + i + date3 ;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1","myChannel", importance);
            channel.setDescription("new message");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public MessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    public void setMessageAdapter(MessageAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
    }

    public ContactItemAdapter getContactItemAdapter() {
        return contactItemAdapter;
    }

    public void setContactItemAdapter(ContactItemAdapter contactItemAdapter) {
        this.contactItemAdapter = contactItemAdapter;
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public boolean isInMessages() {
        return isInMessages;
    }

    public void setInMessages(boolean inMessages) {
        isInMessages = inMessages;
    }

    public ListView getMessagesList() {
        return messagesList;
    }

    public void setMessagesList(ListView messagesList) {
        this.messagesList = messagesList;
    }

    public List<ContactItem> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactItem> contacts) {
        this.contacts = contacts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }
}