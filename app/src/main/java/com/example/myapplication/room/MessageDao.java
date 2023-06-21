package com.example.myapplication.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.elements.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    List<Message> index();

    @Query("SELECT * FROM message WHERE id = :id")
    Message get(int id);

    @Query("SELECT * FROM message WHERE chatId = :chatId")
    List<Message> getMessageList(int chatId);

    @Insert
    void insert(Message... messages);

    @Update
    void update(Message... messages);

    @Delete
    void delete(Message... messages);

    @Query("DELETE FROM message")
    void deleteAll();

}



