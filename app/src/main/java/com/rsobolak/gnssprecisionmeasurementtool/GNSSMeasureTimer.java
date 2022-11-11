package com.rsobolak.gnssprecisionmeasurementtool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GNSSMeasureTimer extends TimerTask {
    private TextView textView;
    private AppCompatActivity activity;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double longitude = null;
    private Double latitude = null;
    private Float accuracy = null;
    private int id = 0;
    private File outFile;
    public  boolean isRunning=false;

    private final File PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

    GNSSMeasureTimer(AppCompatActivity activity) {
        this.activity = activity;
        this.textView = activity.findViewById(R.id.textView);
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyy'T'HH.mm.ss");
        String filename = "GNSS_data_" + s.format(d);
        this.outFile = new File(PATH+"/"+filename+".csv");

        ArrayList<Pair<String, Integer>> permissionsToAskFor = new ArrayList<Pair<String, Integer>>();

        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            activity.requestPermissions(new String [] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2));
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            activity.requestPermissions(new String [] {Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.READ_EXTERNAL_STORAGE, 3));
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            activity.requestPermissions(new String [] {Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 4);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.MANAGE_EXTERNAL_STORAGE, 4));
        try {
            outFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                accuracy = location.getAccuracy();
            }
        };
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            activity.requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            permissionsToAskFor.add(new Pair<>(Manifest.permission.ACCESS_FINE_LOCATION, 1));
        }

        if (permissionsToAskFor.size() > 0)
        {
            String[] permissions = permissionsToAskFor.stream().map(el -> el.first).toArray(String[]::new);
            activity.requestPermissions(permissions, 6);
        }

        StartLocationUpdates();
    }

    public void StartLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "You have to grant location permissions to start tracking", Toast.LENGTH_LONG).show();
            }
            else
            {
                this.locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 0, this.locationListener);
                isRunning=true;
            }
    }

    @Override
    public void run() {
        if(latitude == null || longitude == null || accuracy == null) return;
        id++;
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        writeResultsToFile(d);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(s.format(d) + "\nId = " + id + String.format("\nLongitude = %.6f", longitude) + String.format("\nLatitude = %.6f", latitude) + "\nAccuracy = " + accuracy);
            }
        });

    }

    private void writeResultsToFile(Date date) {
        try {
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outFile, true), "utf-8"), 10240);
            out.write(Integer.toString(id)+",");
            out.write(Double.toString(latitude)+",");
            out.write(Double.toString(longitude)+",");
            out.write(Float.toString(accuracy)+",");
            out.write(s.format(date));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
