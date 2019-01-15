package com.pickapp.driverapp;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
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

public class StatsFragment extends Fragment {


    //  private List<Ride> driversRideList = new ArrayList<Ride>();
    private Backend backend;
    private TextView textViewKm;
    private TextView textViewEarnings;

    private ProgressBar goalsProgressBar;
    private WebView webView;

    private int totalKms;
    private Driver driver;

    String earnings;

    LineChartData data;
    private LineChartView lineChartView;
    String[] axisData = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31"};
    int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_driver_stats, container, false);

        findViews(view);

        backend = BackendFactory.getInstance();

        getActivity().setTitle("View Stats");
        String email = getArguments().getString("email");
        String password = getArguments().getString("password");

        //  Driver driver = backend.getDriver(email, password);

        driver = new Driver();
        goalsProgressBar.setMax(10000);
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
                        // gets all the kms that this driver drove
                        totalKms = backend.totalKmsForDriver(drv[0]);
                        return null;

                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        textViewKm.setText(Integer.toString(totalKms) + " KM");
                    }
                }.execute(driver);

                new AsyncTask<Driver, Void, Void>() {

                    @Override
                    protected Void doInBackground(Driver... drv) {
                        earnings = backend.getMonthlyEarnings(driver) + " shekels of goal";

                        return null;

                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        textViewEarnings.setText(earnings);
                    }
                }.execute(driver);

                new AsyncTask<Driver, Void, Void>() {

                    @Override
                    protected Void doInBackground(Driver... drv) {
                        goalsProgressBar.setProgress(backend.getMonthlyEarnings(driver));
                        return null;

                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute(driver);

                new AsyncTask<Driver, Void, Void>() {

                    @Override
                    protected Void doInBackground(Driver... drv) {
                        yAxisData = backend.getMonthlyKms(drv[0]);
                        return null;

                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        makeLineGraph();
                        lineChartView.setLineChartData(data);
                        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
                        viewport.top = 150;
                        lineChartView.setMaximumViewport(viewport);
                        lineChartView.setCurrentViewport(viewport);
                    }
                }.execute(driver);
            }

        }.execute(email, password);

        return view;
    }

    public void findViews(View v) {
        textViewKm = (TextView) v.findViewById(R.id.textViewKm);
        textViewEarnings = (TextView) v.findViewById(R.id.textViewEarnings);

        goalsProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        lineChartView = (LineChartView) v.findViewById(R.id.chart);

    }

    private void makeLineGraph() {
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#0D47A1"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Kilometers");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

    }

    private double calcEarnings(int kms) {
        return (kms / 78.74) * 0.3;
    }
}