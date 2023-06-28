package com.example.myapplication.api;

import java.util.List;

public class UserArr {
    private List<User> users;

    public UserArr(List<User> users) {
        this.users = users;
    }

    public UserArr() {
    }


    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
