package com.example.heartbeatreader;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sample {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "heart_rate")
    public String heartRate;

    @ColumnInfo(name = "time_stamp")
    public String timeStamp;
}
