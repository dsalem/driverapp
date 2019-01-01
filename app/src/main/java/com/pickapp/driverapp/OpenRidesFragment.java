package com.pickapp.driverapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import adapters.RidesAdapter;
import model.model.backend.Backend;
import model.model.datasource.BackendFactory;
import model.model.entities.Driver;
import model.model.entities.Ride;

public class OpenRidesFragment extends Fragment {

    private List<Ride> rideList;
    private RidesAdapter adapter;
    private Backend backend;
    private Ride ride;
    ListView myListView;
    private Spinner filter;
    private Button callButton;
    private Button smsButton;
    private Button pickButton;
    private Button rideCompleteButton;
    private TextView nameAndDestenation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // String email = getArguments().getString("email");
        // String password = getArguments().getString("password");
        getActivity().setTitle("Choose a ride");
        View view = inflater.inflate(R.layout.fragment_open_rides, container, false);
        backend = BackendFactory.getInstance();
        rideList = backend.getRideList();

        // listView

        adapter = new RidesAdapter(view.getContext(), rideList);

        findViews(view);
        setLiteners();

        // intializing spinner
        String[] items = new String[]{"all", "500", "250", "100", "50"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        filter.setAdapter(arrayAdapter);

        myListView.setAdapter(adapter);
        registerForContextMenu(myListView);

        return view;

    }


    public void findViews(View v) {
        myListView = (ListView) v.findViewById(R.id.ride_list_view);
        filter = (Spinner) v.findViewById(R.id.filter_spinner);
        callButton = (Button) v.findViewById(R.id.call);
        smsButton = (Button) v.findViewById(R.id.send_message);
        pickButton = (Button) v.findViewById(R.id.choose_ride);
        rideCompleteButton = (Button) v.findViewById(R.id.ride_completed);
        nameAndDestenation = (TextView) v.findViewById(R.id.name_and_destination);

    }

    public void setLiteners() {

        myListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ride = (Ride) myListView.getItemAtPosition(position);
                nameAndDestenation.setText(ride.getName() + " wants to go to " + ride.getDestination());
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ride.setStatus(Ride.ClientRequestStatus.HANDLING);
                ride.setStartTime(new Date());
                backend.updateRide(ride, new Backend.Action() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(v.getContext(), "Ride have been approved\n passenger is awaiting at:\n\t " + ride.getLocation(), Toast.LENGTH_LONG).show();

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

        rideCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
