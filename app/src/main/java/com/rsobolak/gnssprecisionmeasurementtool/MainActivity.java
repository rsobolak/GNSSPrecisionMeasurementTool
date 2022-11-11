package com.rsobolak.gnssprecisionmeasurementtool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PackageManagerCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.security.Permission;
import java.time.Instant;
import java.util.Arrays;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private final int FREQUENCY = 5;
    private Button startStopButton;
    private boolean running;
    private Timer timer;
    private GNSSMeasureTimer gnssTimerTask;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startStopButton = (Button) findViewById(R.id.startStopButton);
        startStopButton.setText("Start");
        running = false;
        textView = (TextView) findViewById(R.id.textView);
        textView = (TextView) findViewById(R.id.textView);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(running) {
                    timer.cancel();
                    timer.purge();
                    running = false;
                    startStopButton.setText("Start");
                } else {
                    timer = new Timer(true);
                    gnssTimerTask = new GNSSMeasureTimer((AppCompatActivity) v.getContext());
                    timer.scheduleAtFixedRate(gnssTimerTask, 0, FREQUENCY * 1000);
                    running = true;
                    startStopButton.setText("Stop");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!gnssTimerTask.isRunning && requestCode == 6)
        {
            int idx = Arrays.asList(permissions).indexOf(Manifest.permission.ACCESS_FINE_LOCATION);
            if (grantResults[idx] == PackageManager.PERMISSION_GRANTED)
            {
                gnssTimerTask.StartLocationUpdates();
            }
            else
            {
                Toast.makeText(this, "You need to grant location permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}


