package com.rsobolak.gnssprecisionmeasurementtool;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class GNSSMeasureTimer extends TimerTask {
    private TextView textView;
    private AppCompatActivity activity;

    GNSSMeasureTimer(AppCompatActivity activity) {
        this.activity = activity;
        this.textView = activity.findViewById(R.id.textView);
    }

    @Override
    public void run() {
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(s.format(d));
            }
        });

    }
}
