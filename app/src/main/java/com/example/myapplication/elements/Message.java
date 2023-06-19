package com.example.myapplication.elements;

public class Message {
    private String date;
    private String content;
    private boolean me;

    public Message() {
    }

    public Message(String date, String content, boolean me) {
        this.date = date;
        this.content = content;
        this.me = me;
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

