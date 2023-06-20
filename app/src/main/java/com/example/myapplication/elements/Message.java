package com.example.myapplication.elements;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;

    private int chatId;
    private String content;
    private boolean me;

    public void setMe(boolean me) {
        this.me = me;
    }

    public Message() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public Message(String date, String content, boolean me, int chatId) {
        this.date = date;
        this.content = content;
        this.me = me;
        this.chatId = chatId;
    }


    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public boolean getMe() {
        return me;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(boolean me) {
        this.me = me;
    }
}

