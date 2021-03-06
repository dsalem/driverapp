package com.pickapp.driverapp;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.DriverHistoryAdapter;
import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Driver;
import model.model.entities.Ride;

public class DriverHistoryFragment extends Fragment {


    private List<Ride> driversRideList;
    private DriverHistoryAdapter adapter;
    private Backend backend;
    Driver driver;
    View view;
    ListView myListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_driver_history, container, false);
        backend = BackendFactory.getInstance();

        getActivity().setTitle("Add contact");
        // listView
        myListView = (ListView) view.findViewById(R.id.ride_list_view);

        String email = getArguments().getString("email");
        String password = getArguments().getString("password");
        driversRideList = new ArrayList<Ride>();
        driver = new Driver();
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... str) {
                driver = backend.getDriver(str[0], str[1]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                new AsyncTask<Driver, Void, Void>() {

                    @Override
                    protected Void doInBackground(Driver... drv) {

                        // gets all the rides that this driver took
                        driversRideList = backend.getDriverHistoryList(drv[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        // sets the view after getting all informayion from DB
                        adapter = new DriverHistoryAdapter(view.getContext(), driversRideList);
                        myListView.setAdapter(adapter);
                        myListView.setEmptyView(view.findViewById(R.id.no_history));
                        registerForContextMenu(myListView);
                    }
                }.execute(driver);
            }
        }.execute(email, password);

        return view;
    }
}
