package com.rsobolak.gnssprecisionmeasurementtool;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.Instant;
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

    protected void SaveGNSSData() {

    }

}


