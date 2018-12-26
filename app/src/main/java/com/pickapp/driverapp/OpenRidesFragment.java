package com.pickapp.driverapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import adapters.RidesAdapter;
import model.model.backend.Backend;
import model.model.datasource.BackendFactory;
import model.model.entities.Ride;

public class OpenRidesFragment extends Fragment {

    private List<Ride> rideList;
    private RidesAdapter adapter;
    private Backend backend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_open_rides,container,false);
        backend = BackendFactory.getInstance();
        rideList = backend.getRideList();
        // listView
        ListView myListView = (ListView) view.findViewById(R.id.ride_list_view);
        adapter = new RidesAdapter(view.getContext(),rideList);
        myListView.setAdapter(adapter);
        registerForContextMenu(myListView);

        return view;
    }



}
