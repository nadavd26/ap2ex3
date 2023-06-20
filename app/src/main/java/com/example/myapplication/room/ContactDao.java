package com.example.myapplication.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.elements.ContactItem;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contactitem")
    List<ContactItem> index();

    @Query("SELECT * FROM contactitem WHERE id = :id")
    ContactItem get(int id);

    @Insert
    void insert(ContactItem... contactDaoElements);

    @Update
    void update(ContactItem... contactDaoElements);

    @Delete
    void delete(ContactItem... contactDaoElements);
}
