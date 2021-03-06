package adapters;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pickapp.driverapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            holder.addContact = (ImageButton) v.findViewById(R.id.add_button);
            holder.nameOfContact = (TextView) v.findViewById(R.id.contact_name);
            holder.phoneOfContact = (TextView) v.findViewById(R.id.contact_phone);
            v.setTag(holder);
        } else
            holder = (RideHistoryHolder) v.getTag();

        final Ride p = ridesList.get(position);

        holder.nameOfContact.setText(p.getName());
        holder.phoneOfContact.setText(p.getPhone());
        Random rand = new Random();
        holder.addContact.setBackgroundColor(Color.argb(200
                , rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        holder.addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create dialog if you want to save contact
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Save contact?")
                        .setMessage("Are you sure you want to save this contact?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addToContacts(p);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        return v;
    }

    public void resetData() {
        ridesList = origRideList;
    }

    public void addToContacts(Ride ride) {

        // OPTION A
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, ride.getName()) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, ride.getName())
                .build());

        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, ride.getPhone()) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        //INSERT EMAIL
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, ride.getEmail())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());
        Uri newContactUri = null;
        
        //PUSH EVERYTHING TO CONTACTS
        try {
            ContentProviderResult[] res = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                newContactUri = res[0].uri;
                //02-20 22:21:09 URI added contact:content://com.android.contacts/raw_contacts/612

            } else
                Toast.makeText(context, "Contact not added.", Toast.LENGTH_LONG).show();
        } catch (RemoteException e) {
            // error
            Toast.makeText(context, "Error (1) adding contact.", Toast.LENGTH_LONG).show();
            newContactUri = null;
        } catch (OperationApplicationException e) {
            // error
            Toast.makeText(context, "Error (2) adding contact.", Toast.LENGTH_LONG).show();
            newContactUri = null;
        }
        Toast.makeText(context, "Contact added to system contacts.", Toast.LENGTH_LONG).show();

        if (newContactUri == null) {
            Toast.makeText(context, "Error creating contact", Toast.LENGTH_LONG).show();
        }

        /*
        // OPTION B
        // implement adding the contact to phone
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        // send the phone and name and email to the adding contract activity
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, ride.getPhone())
                .putExtra(ContactsContract.Intents.Insert.NAME, ride.getName())
                .putExtra(ContactsContract.Intents.Insert.EMAIL, ride.getEmail())
                // to return to this app after saving contact
                .putExtra("finishActivityOnSaveCompleted", true);

        context.startActivity(intent);*/
    }

    /**
     * check if the phone already exist in the contracts
     *
     * @param context the activity context
     * @param number  phone number to check
     * @return bool exist or not
     * @see <a href="https://stackoverflow.com/questions/3505865/android-check-phone-number-present-in-contact-list-phone-number-retrieve-fr">stackoverflow</a>
     */
    private boolean isContactSavedOnPhone(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    /* *********************************
     * We use the holder pattern
     * It makes the view faster and avoid finding the component
     * **********************************/

    private static class RideHistoryHolder {
        public ImageButton addContact;
        public TextView nameOfContact;
        public TextView phoneOfContact;
    }

    private class RideFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Ride> nRideList = new ArrayList<Ride>();
            for (Ride r : ridesList
                    ) {
                if (!isContactSavedOnPhone(context, r.getPhone()))
                    nRideList.add(r);
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
