package adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Driver;
import model.model.entities.Ride;

public class RidesAdapter extends ArrayAdapter<Ride> implements Filterable {

    private List<Ride> ridesList;
    private Context context;
    private Filter myRideFilter;
    private List<Ride> origRideList;
    private Location location = null;
    private Backend backend;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;


    // Define a listener that responds to location updates
    LocationListener locationListener;


    public RidesAdapter(@NonNull Context context, List<Ride> resource) {
        super(context, R.layout.location_row_layout, resource);
        this.context = context;
        this.origRideList = resource;
        this.ridesList = resource;
        backend = BackendFactory.getInstance();

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
            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... str) {
                    ridesList = backend.getWaitingList();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute("");
            initiateLocation();
            location = getGpsLocation();

            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0 || constraint.equals("all")) {
                // No filter implemented we return all the list

            } else {
                // We perform filtering operation
                List<Ride> temp = new ArrayList<Ride>();
                for (Ride p : ridesList) {
                    if (calcDistanceToDestination(location, p.getLocation()) <= Float.valueOf(constraint.toString()))
                        temp.add(p);

                }
                ridesList = temp;
            }

            results.values = ridesList;
            results.count = ridesList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                ridesList.clear();
                notifyDataSetInvalidated();
            } else {
                ridesList = (List<Ride>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    public void initiateLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // got premission in DriverActivity
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        return locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
    }

    public float calcDistanceToDestination(Location startLocation, String destination) {
        Context context = this.context;

        LatLng latLngDestination = getLocationFromAddress(context, destination);
        double endLatitude = latLngDestination.latitude;
        double endLongitude = latLngDestination.longitude;

        Location locationB = new Location("point B");
        locationB.setLatitude(endLatitude);
        locationB.setLongitude(endLongitude);

        float distance = startLocation.distanceTo(locationB);
        return (distance / 1000);

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
