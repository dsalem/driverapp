package com.pickapp.driverapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pickapp.driverapp.R;

import java.util.ArrayList;
import java.util.List;

import adapters.DriverHistoryAdapter;
import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Driver;
import model.model.entities.Ride;

public class statsFragment extends Fragment {


    private List<Ride> driversRideList = new ArrayList<Ride>();
    //private DriverHistoryAdapter adapter;
    private Backend backend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);
        backend = BackendFactory.getInstance();

        getActivity().setTitle("View Stats");
        // listView
       // ListView myListView = (ListView) view.findViewById(R.id.ride_list_view);

        String email = getArguments().getString("email");
        String password = getArguments().getString("password");

        Driver driver = backend.getDriver(email, password);

        // gets all the rides that this driver took
        driversRideList = backend.getDriverHistoryList(driver);




        //adapter = new DriverHistoryAdapter(view.getContext(), driversRideList);
        //myListView.setAdapter(adapter);
       // myListView.setEmptyView(view.findViewById(R.id.no_history));
        //registerForContextMenu(myListView);

        return view;
    }


}