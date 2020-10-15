package com.example.heartbeatreader;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Sample.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract SampleDao sampleDao();
}
