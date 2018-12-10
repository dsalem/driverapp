package com.pickapp.driverapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
            // nBuilder.setSmallIcon(R.drawable.services);
            nBuilder.setContentTitle("New Ride");
            nBuilder.setContentText("you have got a new ride waiting for you!");
            Notification notification = nBuilder.build();
            Object obj = getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManager notificationManager = (NotificationManager) obj;
            notificationManager.notify(1234, nBuilder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInForeground() {

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

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
