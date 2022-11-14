package com.rsobolak.gnssprecisionmeasurementtool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.security.Permission;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private Button startStopButton;
    private boolean running;

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

        ArrayList<Pair<String, Integer>> permissionsToAskFor = new ArrayList<Pair<String, Integer>>();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            context.requestPermissions(new String [] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2));
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            context.requestPermissions(new String [] {Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.READ_EXTERNAL_STORAGE, 3));
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            context.requestPermissions(new String [] {Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 4);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.MANAGE_EXTERNAL_STORAGE, 4));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            context.requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.ACCESS_FINE_LOCATION, 1));
        }

        if (permissionsToAskFor.size() > 0)
        {
            String[] permissions = permissionsToAskFor.stream().map(el -> el.first).toArray(String[]::new);
            this.requestPermissions(permissions, 6);
        }

        startStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(running) {
                    v.getContext().stopService(new Intent(v.getContext(), GNSSMeasureService.class));
                    running = false;
                    startStopButton.setText("Start");
                } else {
                    v.getContext().startService(new Intent(v.getContext(), GNSSMeasureService.class));
                    running = true;
                    startStopButton.setText("Stop");
                }
            }
        });
    }

/*    @Override
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
    }*/
}


