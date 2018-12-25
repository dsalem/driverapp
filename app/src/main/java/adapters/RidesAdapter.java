package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pickapp.driverapp.R;

import java.util.ArrayList;
import java.util.List;

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
        holder.rideLocationView.setText(p.getName());
        holder.distView.setText("" + p.calcDistanceToDestination());


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
            myRideFilter = new PlanetFilter();

        return myRideFilter;
    }

    private class PlanetFilter extends Filter {

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
}
