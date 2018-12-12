package com.pickapp.driverapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.List;

import model.model.backend.Backend;
import model.model.datasource.BackendFactory;
import model.model.entities.Ride;

public class NotificationService extends Service {

    Backend backend = BackendFactory.getInstance();

    NotificationManager notificationManager;

    // @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();

        // checks if the phone supports this version for foreground notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startInForeground();
        } else {
            Notification.Builder nBuilder = new Notification.Builder(getBaseContext());
             nBuilder.setSmallIcon(R.drawable.ic_launcher_background);
            nBuilder.setContentTitle("New Ride");
            nBuilder.setContentText("you have got a new ride waiting for you!");
            Notification notification = nBuilder.build();
            startForeground(1234, notification);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInForeground() {
        String NOTIFICATION_CHANNEL_ID = "DriverApplication";
        String channelName = "DriverService";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.WHITE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("DriverApp is running in background")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1234, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        backend.notifyToRideList(new Backend.NotifyDataChange<List<Ride>>() {

            @Override
            public void OnDataChanged(List<Ride> ride) {
                Toast.makeText(getBaseContext(), ride.size() + "People waiting for pickup", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getBaseContext(), "Error\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    // stopNoticfication
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
