package com.example.heartbeatreader;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface SampleDao {
    @Query("SELECT * FROM sample")
    List<Sample> getAll();

    @Insert
    void insertAll(Sample... samples);

    @Delete
    void delete(Sample sample);
}
