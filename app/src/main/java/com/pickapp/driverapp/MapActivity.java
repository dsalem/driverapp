package com.pickapp.driverapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Date;

import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Ride;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Ride ride;
    private Backend backend = BackendFactory.getInstance();
    private Button rideCompleteButton;
    private Button callButton;
    private Button smsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String rideId = getIntent().getStringExtra("rideId");
        ride = backend.getRider(rideId);
        View v = getWindow().getDecorView();
        callButton = (Button) v.findViewById(R.id.call);
        smsButton = (Button) v.findViewById(R.id.send_message);
        rideCompleteButton = (Button) v.findViewById(R.id.ride_completed);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                //intent.setData(Uri.parse("tel:0123456789"));
                intent.setData(Uri.parse("tel:" + ride.getPhone()));
                startActivity(intent);
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String smsNumber = ride.getPhone();
                String smsText = "Hey " + ride.getName() + " I'm on my way to " + ride.getLocation() + " to take you to " + ride.getDestination();

                Uri uri = Uri.parse("smsto:" + smsNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", smsText);
                startActivity(intent);
                //if we want to send a text message without opening the text messaging app
                /*String messageToSend = "Hey" + ride.getName() + "I'll be by you in 2 minutes" ;
                String number = ride.getPhone();

                SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);*/
           }
        });

        rideCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ride.setStatus(Ride.ClientRequestStatus.CLOSED);
                ride.setStartTime(new Date());
                backend.updateRide(ride, new Backend.Action() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(v.getContext(), "Ride complete!", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(v.getContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgress(String status, double percent) {
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}