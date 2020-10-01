package com.example.heartbeatreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.room.Room;

public class MainActivity extends WearableActivity implements SensorEventListener{

    private static final String TAG = "MainActivity";

    private Button btnStartReader;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private boolean readerActivated = false;
    private TextView mHeartRateView;

    public void onResume(){
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartReader = findViewById(R.id.btnStartReader);
        mHeartRateView = findViewById(R.id.heartRateView);

        btnStartReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!readerActivated) {
                    btnStartReader.setBackgroundColor(Color.GREEN);
                    btnStartReader.setText("STOP");
                    readerActivated = true;
                    startMeasure();
                }
                else {
                    btnStartReader.setBackgroundColor(Color.GRAY);
                    btnStartReader.setText("START");
                    readerActivated = false;
                    stopMeasure();
                }

            }
        });

        // Enables Always-on
        setAmbientEnabled();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        //DB
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();
        User user = new User();
        user.firstName = "Alejo";
        user.lastName = "Almeida";
        user.uid = 1;
        db.userDao().insertAll(user);

        User newUser = db.userDao().findByName("Alejo", "Almeida");

    }

    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this,
                mHeartRateSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG, " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
    }

    private void stopMeasure() {
        mSensorManager.unregisterListener(this);
        Log.d(TAG, " Sensor stopped");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int) event.values[0];
            mHeartRateView.setText(msg);
        }
    }

}
