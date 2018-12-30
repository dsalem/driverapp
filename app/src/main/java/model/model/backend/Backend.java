package model.model.backend;

import java.util.List;

import com.google.firebase.database.*;

import model.model.datasource.Firebase_DBManager;
import model.model.entities.Driver;
import model.model.entities.Ride;

public interface Backend {

    // Driver methods
    void addDriver(final Driver Driver, final Action action);

    void removeDriver(long id, final Action action);

    void updateRide(final Driver toUpdate, final Action action);

    void isDriverInDataBase(final Driver driver, final Action action);

    void isDriversPasswordCorrect(String dEmail ,String dPassword, final Action action);

    List<Driver> getDriverList();

    // Rider methods
    void addRide(final Ride Ride, final Action action);

    void removeRide(String phone, final Action action);

    void updateRide(final Ride toUpdate, final Action action);

    List<Ride> getRideList();

    void notifyToRideList(final NotifyDataChange<List<Ride>> notifyDataChange);


    public interface Action<T> {
        void onSuccess();

        void onFailure(Exception exception);

        void onProgress(String status, double percent);
    }

    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }

}