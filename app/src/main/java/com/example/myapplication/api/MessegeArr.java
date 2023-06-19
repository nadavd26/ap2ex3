package com.example.myapplication.api;

import com.example.myapplication.elements.Message;

import java.util.List;

public class MessegeArr {
    private List<MessageServer> messages;

    public MessegeArr() {
    }

    public MessegeArr(List<MessageServer> messages) {
        this.messages = messages;
    }

    public List<MessageServer> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageServer> messages) {
        this.messages = messages;
    }
}
