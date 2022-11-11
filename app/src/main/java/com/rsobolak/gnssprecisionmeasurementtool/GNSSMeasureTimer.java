package com.rsobolak.gnssprecisionmeasurementtool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class GNSSMeasureTimer extends TimerTask {
    private TextView textView;
    private AppCompatActivity activity;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double longitude = null;
    private Double latitude = null;
    private Float accuracy = null;
    private int id = 0;

    GNSSMeasureTimer(AppCompatActivity activity) {
        this.activity = activity;
        this.textView = activity.findViewById(R.id.textView);
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                accuracy = location.getAccuracy();
            }
        };
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    @Override
    public void run() {
        if(latitude == null || longitude == null || accuracy == null) return;
        id++;
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(s.format(d) + "\nId = " + id + String.format("\nLongitude = %.6f", longitude) + String.format("\nLatitude = %.6f", latitude) + "\nAccuracy = " + accuracy);
            }
        });

    }
}
