package com.pickapp.driverapp;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Ride;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Ride ride;
    private Backend backend = BackendFactory.getInstance();
    private Button rideCompleteButton;
    private ImageButton callButton;
    private ImageButton smsButton;
    private Location myLocation;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;


    // Define a listener that responds to location updates
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String rideId = getIntent().getStringExtra("rideId");
        ride = backend.getRider(rideId);
        View v = getWindow().getDecorView();
        callButton = (ImageButton) v.findViewById(R.id.call);
        smsButton = (ImageButton) v.findViewById(R.id.send_message);
        rideCompleteButton = (Button) v.findViewById(R.id.ride_completed);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                myLocation = getGpsLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        map.setMyLocationEnabled(true);
        myLocation = getGpsLocation();

        LatLng driverLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        LatLng rideLocation = getLocationFromAddress(this, ride.getLocation());

        map.addMarker(new MarkerOptions().position(driverLocation).title("Your location"));

        map.addMarker(new MarkerOptions().position(rideLocation).title("Passenger location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(driverLocation));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private Location getGpsLocation() {
        //     Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // got premission in DriverActivity
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        return locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
    }

    public LatLng getLocationFromAddress(Context context, String inputtedAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng resLatLng = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(inputtedAddress, 5);
            if (address == null) {
                return null;
            }

            if (address.size() == 0) {
                return null;
            }

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            resLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return resLatLng;
    }
}
