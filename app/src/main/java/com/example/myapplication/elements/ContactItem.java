package com.example.myapplication.elements;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.example.myapplication.R;

@Entity
public class ContactItem {
    @PrimaryKey
    private int id;
    private String date;
    private String lastMessage;
    private String displayName;
    private String profilePic;

    public ContactItem() {
    }

    public ContactItem(int id, String date, String lastMessage, String displayName, String profilePic) {
        this.date = date;
        this.id = id;
        this.lastMessage = lastMessage;
        this.displayName = displayName;
        this.profilePic = profilePic;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfilePic(String profilePic) {
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

    public Bitmap getProfilePicBitmap() {
        byte[] decodedString = Base64.decode(profilePic, Base64.DEFAULT);
        Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return profilePicBitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }
}