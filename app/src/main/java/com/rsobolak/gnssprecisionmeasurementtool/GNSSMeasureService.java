package com.rsobolak.gnssprecisionmeasurementtool;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class GNSSMeasureService extends Service {
    public static final String CHANNEL_ID = "GNSSMeasureServiceChannel";
    private final File PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    private final int FREQUENCY = 5;
    private final int MAX_ENTRIES = 1500;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double longitude = null;
    private Double latitude = null;
    private Float accuracy = null;
    private int id = 0;
    private File outFile;
    private Thread thread;
    private boolean running = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        createChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GNSS Measure Service")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


        running = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyy'T'HH.mm.ss");
        String filename = "GNSS_data_" + s.format(d);
        outFile = new File(PATH + "/" + filename + ".csv");


        try {
            outFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                accuracy = location.getAccuracy();
            }
        };

            StartLocationUpdates();

        thread = new Thread(){
            public void run() {
                for (int i = 0; i < MAX_ENTRIES; i++) {
                    if (!running) break;
                    if (latitude == null || longitude == null || accuracy == null) {
                        try {
                            TimeUnit.SECONDS.sleep(FREQUENCY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    id++;
                    Date date = new Date();
                    writeResultsToFile(date);
                    try {
                        TimeUnit.SECONDS.sleep(FREQUENCY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                running = false;
            }
        };

        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void StartLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
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

    private void createChannel() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_ID,  //name of the channel
                NotificationManager.IMPORTANCE_HIGH);   //importance level

        // Configure the notification channel.
        mChannel.setDescription("Channel for GNSS Measurement Service");
        mChannel.enableLights(true);

        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        mChannel.setShowBadge(true);
        assert nm != null;
        nm.createNotificationChannel(mChannel);
    }
}
