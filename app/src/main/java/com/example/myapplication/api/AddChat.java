package com.example.myapplication.api;

public class AddChat {
    private String id;
    private User user;


    public AddChat(String id, User user) {
        this.id = id;
        this.user = user;
    }

    public AddChat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
