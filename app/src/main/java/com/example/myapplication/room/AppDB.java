package com.example.myapplication.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.elements.ContactItem;
import com.example.myapplication.elements.Message;


@Database(entities = {ContactItem.class, Message.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract MessageDao messageDao();
    public abstract ContactDao contactDao();
}
