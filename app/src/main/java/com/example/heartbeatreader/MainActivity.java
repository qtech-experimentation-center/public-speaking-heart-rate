package com.example.heartbeatreader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private Button btnStartReader;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private boolean readerActivated = false;
    private TextView mHeartRateView;
    private int minBufferSize;
    private DataOutput dataOutputStream;

    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRecordAudioPermission();

        btnStartReader = findViewById(R.id.btnStartReader);
        mHeartRateView = findViewById(R.id.heartRateView);

        minBufferSize = 2;
        dataOutputStream = null;

        btnStartReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!readerActivated) {
                    try {
                        recordAudio();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btnStartReader.setBackgroundColor(Color.GREEN);
                    btnStartReader.setText("STOP");
                    readerActivated = true;
                    startMeasure();

                } else {
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

    public void recordAudio() throws IOException {
        String fileName = (LocalDateTime.now().toString()).concat("-test.pcm");
        File file = new File(Environment.getExternalStorageDirectory(), fileName);

        int sampleFreq = 8000;//(Integer)spFrequency.getSelectedItem();

        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(sampleFreq,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleFreq,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);

            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording();
                //while (recording) {
                for (int i = 0; i < 100; i++) {
                    int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                    for (int j = 0; j < numberOfShort; j++) {
                        dataOutputStream.writeShort(audioData[j]);
                    }
                }
            }
            audioRecord.stop();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestRecordAudioPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

}