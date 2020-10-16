package com.example.heartbeatreader;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Sample.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SampleDao sampleDao();
}
