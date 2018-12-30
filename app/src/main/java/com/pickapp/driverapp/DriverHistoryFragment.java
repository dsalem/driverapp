package com.pickapp.driverapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

import adapters.DriverHistoryAdapter;
import model.model.backend.Backend;
import model.model.datasource.BackendFactory;
import model.model.entities.Driver;
import model.model.entities.Ride;

public class DriverHistoryFragment extends Fragment {


    private List<Ride> driversRideList;
    private DriverHistoryAdapter adapter;
    private Backend backend;
    private Spinner filter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_history,container,false);
        backend = BackendFactory.getInstance();


        // listView
        ListView myListView = (ListView) view.findViewById(R.id.ride_list_view);

        String email = getArguments().getString("email");
        String password = getArguments().getString("password");
        Driver driver = new Driver();
        for (Driver d: backend.getDriverList()
             ) {
            if(d.getPassword().equals(password) && d.getEmailAddress().equals(email))
                driver = d;
        }

        // gets all the rides that this driver took
        for (Ride r : backend.getRideList()
             ) {
            if(r.getStatus() == Ride.ClientRequestStatus.CLOSED)
             if(r.getDriverName().equals(driver.getFirstName()))
                driversRideList.add(r);
        }

        adapter = new DriverHistoryAdapter(view.getContext(),driversRideList);
        myListView.setAdapter(adapter);
        registerForContextMenu(myListView);

        return view;
    }


}
