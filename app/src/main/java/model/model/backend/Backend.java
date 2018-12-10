package model.model.backend;

import java.util.List;

import com.google.firebase.database.*;
import model.model.entities.Driver;
import model.model.entities.Ride;

public interface Backend {

    // Driver methods
    void addDriver(final Driver Driver, final Action action);

    void removeDriver(long id, final Action action);

    void updateRide(final Driver toUpdate, final Action action);

    void isDriverInDataBase(final Driver driver, final Action action);

    // Rider methods
    void addRide(final Ride Ride, final Action action);

    void removeRide(long phone, final Action action);

    void updateRide(final Ride toUpdate, final Action action);

    public interface Action<T> {
        void onSuccess();

        void onFailure(Exception exception);

        void onProgress(String status, double percent);
    }
}