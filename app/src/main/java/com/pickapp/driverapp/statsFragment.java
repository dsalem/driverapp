package com.pickapp.driverapp;

import android.app.Fragment;
import android.graphics.Color;
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

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
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

    private LineChartView lineChartView;
    String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"};
    int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};


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
        double totalEarnings;
        totalKms = backend.totalKmsForDriver(driver);
        textViewKm.setText(Integer.toString(totalKms));
        totalEarnings = calcEarnings(totalKms);

        textViewKm.setText(Integer.toString(totalKms) + " KM");
        goalsProgressBar.setMax(100);
        goalsProgressBar.setProgress((int) totalEarnings);


        yAxisData = backend.getMonthlyEarnings(driver);
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();


        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Sales in millions");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 100;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);


        return view;
    }

    public void findViews(View v) {
        textViewKm = (TextView) v.findViewById(R.id.textViewKm);
        goalsProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        lineChartView = (LineChartView) v.findViewById(R.id.chart);


    }

    private double calcEarnings(int kms) {
        return (kms / 78.74) * 0.3;
    }
}