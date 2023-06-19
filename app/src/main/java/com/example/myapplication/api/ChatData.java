package com.example.myapplication.api;

public class ChatData {
    private String username;

    public ChatData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ChatData() {
    }
}