package adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pickapp.driverapp.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.model.entities.Ride;

public class RidesAdapter extends ArrayAdapter<Ride> implements Filterable {

    private List<Ride> ridesList;
    private Context context;
    private Filter myRideFilter;
    private List<Ride> origRideList;
    private Location location = null;

    // Acquire a reference to the system Location Manager
    LocationManager locationManager;


    // Define a listener that responds to location updates
    LocationListener locationListener;


    public RidesAdapter(@NonNull Context context, List<Ride> resource) {
        super(context, R.layout.location_row_layout, resource);
        this.context = context;
        this.origRideList = resource;
        this.ridesList =  resource;
    }

    public int getCount() {
        return ridesList.size();
    }

    public Ride getItem(int position) {
        return ridesList.get(position);
    }

    public long getItemId(int position) {
        return ridesList.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        RideHolder holder = new RideHolder();

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.location_row_layout, null);

            // Now we can fill the layout with the right values
            holder.rideLocationView = (TextView) v.findViewById(R.id.waiting_at);
            holder.rideLengthView = (TextView) v.findViewById(R.id.ride_length);

            v.setTag(holder);
        } else
            holder = (RideHolder) v.getTag();

        Ride p = ridesList.get(position);
        // ToDo convert the location to smaller format using getPlace from ap1

        holder.rideLocationView.setText(p.getLocation().replaceAll(",", "\n"));
        holder.rideLengthView.setText(Float.toString(p.getLengthOfRide()) + " KM");
        return v;
    }

    public void resetData() {
        ridesList = origRideList;
    }

    /* *********************************
     * We use the holder pattern
     * It makes the view faster and avoid finding the component
     * **********************************/

    private static class RideHolder {
        public TextView rideLocationView;
        public TextView rideLengthView;

    }

    /*
     * We create our filter
     */

    @Override
    public Filter getFilter() {
        if (myRideFilter == null)
            myRideFilter = new RideFilter();

        return myRideFilter;
    }

    private class RideFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Ride> nRideList = new ArrayList<Ride>();

            for (Ride p : ridesList) {
                if (p.getStatus().equals(Ride.ClientRequestStatus.WAITING))
                    nRideList.add(p);
            }
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0 || constraint.equals("all")) {
                // No filter implemented we return all the list

            } else {
                // We perform filtering operation
                for (Ride p : ridesList) {
                    // ToDo calc distance from driver and filter
                   //if (p.getLengthOfRide() <= Float.valueOf( constraint.toString()))
                      //  nRideList.add(p);
                }
            }
            results.values = nRideList;
            results.count = nRideList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                ridesList = (List<Ride>) results.values;
                notifyDataSetChanged();
            }
        }
    }

public void initiateLocation() {
    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
    }

    // Define a listener that responds to location updates
    locationListener = new LocationListener() {
        public void onLocationChanged(Location locat) {
            location = getGpsLocation();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}

    private Location getGpsLocation() {
        //     Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        return locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the location", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public float calcDistanceToDestination(String startLocation, String destination) {

        //String startLocation = ride.getLocation();
        Context context = getApplicationContext();
        LatLng latLngLocation = getLocationFromAddress(context, startLocation);
        double startLatitude = latLngLocation.latitude;
        double startLongitude = latLngLocation.longitude;

        // String destination = ride.getDestination();
        LatLng latLngDestination = getLocationFromAddress(context, destination);
        double endLatitude = latLngDestination.latitude;
        double endLongitude = latLngDestination.longitude;

        Location locationA = new Location("point A");
        locationA.setLatitude(startLatitude);
        locationA.setLongitude(startLongitude);

        Location locationB = new Location("point B");
        locationB.setLatitude(endLatitude);
        locationB.setLongitude(endLongitude);

        float distance = locationA.distanceTo(locationB);
        return (distance / 1000);
        //return 0.52;
    }



}
