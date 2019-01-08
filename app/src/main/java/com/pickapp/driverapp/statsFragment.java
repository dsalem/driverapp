package com.pickapp.driverapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pickapp.driverapp.R;

import java.util.ArrayList;
import java.util.List;

import model.model.backend.Backend;
import model.model.backend.BackendFactory;
import model.model.entities.Driver;
import model.model.entities.Ride;
import model.model.datasource.Firebase_DBManager;

public class statsFragment extends Fragment {


    private List<Ride> driversRideList = new ArrayList<Ride>();
    private Backend backend;
    private TextView textViewKm;
    private ProgressBar goalsProgressBar;
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_driver_stats, container, false);

        findViews(view);

        backend = BackendFactory.getInstance();
       // textViewKm.findViewById(R.id.textViewKm);

        getActivity().setTitle("View Stats");
        String email = getArguments().getString("email");
        String password = getArguments().getString("password");
        Driver driver = backend.getDriver(email, password);

        // gets all the kms that this driver drove

        int totalKms;
        totalKms = backend.totalKmsForDriver(driver);
        textViewKm.setText(Integer.toString( totalKms));


        return view;
    }
    public void findViews(View v) {
        textViewKm = (TextView) v.findViewById(R.id.textViewKm);



    }

}