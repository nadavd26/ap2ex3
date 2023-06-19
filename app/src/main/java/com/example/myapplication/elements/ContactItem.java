package com.example.myapplication.elements;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.example.myapplication.R;

public class ContactItem {
    private String chatId;
    private String date;
    private String lastMessage;
    private String displayName;
    private Bitmap profilePic;

    public ContactItem(String chatId, String date, String lastMessage, String displayName, Bitmap profilePic) {
        this.date = date;
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.displayName = displayName;
        this.profilePic = profilePic;
    }

    public ContactItem(String chatId, String date, String lastMessage, String displayName, String profilePicBase64) {
        this.date = date;
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.displayName = displayName;
        byte[] decodedString = Base64.decode(profilePicBase64, Base64.DEFAULT);
        Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.profilePic = profilePicBitmap;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }
}