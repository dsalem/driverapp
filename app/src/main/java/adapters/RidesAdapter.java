package adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.model.entities.Ride;

public class RidesAdapter extends ArrayAdapter<Ride> implements Filterable {

    private List<Ride> ridesList;
    private Context context;
    private Filter myRideFilter;
    private List<Ride> origRideList;

    public RidesAdapter(@NonNull Context context, List<Ride> resource) {
        super(context,R.layout.location_row_layout, resource);
        this.context = context;
        this.origRideList = resource;
        this.ridesList = resource;
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
            TextView location = (TextView) v.findViewById(R.id.location);
            TextView distView = (TextView) v.findViewById(R.id.dist);


            holder.rideLocationView = location;
            holder.distView = distView;

            v.setTag(holder);
        }
        else
            holder = (RideHolder) v.getTag();

        Ride p = ridesList.get(position);
        // ToDo filter the location to smaller format using getPlace from ap1
        holder.rideLocationView.setText(p.getLocation().trim());
        holder.distView.setText("" + calcDistanceToDestination(p));
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
        public TextView distView;
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
            // ToDo actual work needed here....
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = origRideList;
                results.count = origRideList.size();
            }
            else {
                // We perform filtering operation
                List<Ride> nPlanetList = new ArrayList<Ride>();

                for (Ride p : ridesList) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nPlanetList.add(p);
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
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

    public double calcDistanceToDestination(Ride ride)
    {
        String startLocation = ride.getLocation();
        LatLng latLngLocation = getLocationFromAddress(context,startLocation);
        double startLatitude =latLngLocation.latitude;
        double startLongitude =latLngLocation.latitude;

        String destination = ride.getDestination();
        LatLng latLngDestination = getLocationFromAddress(context,destination);
        double endLatitude =latLngDestination.latitude;
        double endLongitude =latLngDestination.longitude;

        Location locationA= new Location("point A");
        locationA.setLatitude(startLatitude);
        locationA.setLongitude(startLongitude);

        Location locationB = new Location("point B");
        locationB.setLatitude(endLatitude);
        locationB.setLongitude(endLongitude);

        double distance = locationA.distanceTo(locationB);
        return distance;
        //return 0.52;
    }
}
