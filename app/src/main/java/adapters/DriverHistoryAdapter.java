package adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pickapp.driverapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.model.entities.Ride;

public class DriverHistoryAdapter extends ArrayAdapter<Ride> {

    private List<Ride> ridesList;
    private Context context;
    private List<Ride> origRideList;

    public DriverHistoryAdapter(@NonNull Context context, List<Ride> resource) {
        super(context, R.layout.add_contact_row_layout, resource);
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

        RideHistoryHolder holder = new RideHistoryHolder();

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.add_contact_row_layout, null);

            // Now we can fill the layout with the right values
            holder.addContact = (Button) v.findViewById(R.id.add_button);
            holder.nameOfContact = (TextView) v.findViewById(R.id.contact_name);

            v.setTag(holder);
        }
        else
            holder = (RideHistoryHolder) v.getTag();

       final Ride p = ridesList.get(position);

        holder.addContact.setText("Add");
        holder.nameOfContact.setText(p.getName());

        holder.addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToDo create dialog if you want to save contact
                addToContacts(p);
            }
        });
        return v;
    }

    public void resetData() {
        ridesList = origRideList;
    }

    public void addToContacts(Ride ride){
        // ToDo implement adding the contact to phone
    }

    /* *********************************
     * We use the holder pattern
     * It makes the view faster and avoid finding the component
     * **********************************/

    private static class RideHistoryHolder {
        public Button addContact;
        public TextView nameOfContact;
    }

    private class RideFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Ride> nRideList = new ArrayList<Ride>();

            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0 ) {
                // No filter implemented we return all the list
            } else {
                // We perform filtering operation
            }

            results.values = nRideList;
            results.count = nRideList.size();
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
}
