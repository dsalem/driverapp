package com.pickapp.driverapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;

import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Ride;

public class NotificationService extends Service {

    Backend backend = BackendFactory.getInstance();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        backend.notifyToRideList(new Backend.NotifyDataChange<List<Ride>>() {

            @Override
            public void OnDataChanged(List<Ride> ride) {
              try{
                Intent intent = new Intent(getApplicationContext(),MyBroadcastReceiver.class);
                sendBroadcast(intent);
              } catch (Exception e){
                  e.printStackTrace();
              }
              //  Toast.makeText(getBaseContext(), ride.size() + " People waiting for pickup", Toast.LENGTH_LONG).show();
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
