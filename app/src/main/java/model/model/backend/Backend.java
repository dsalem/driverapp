package model.model.backend;

import model.model.datasource.Firebase_DBManager;
import model.model.entities.Driver;
import model.model.entities.Ride;

public interface Backend {

    // Driver methods
    void addDriver(final Driver Driver, final Firebase_DBManager.Action<Long> action);

    void removeDriver(long id, final Firebase_DBManager.Action<Long> action);

    void updateRide(final Driver toUpdate, final Firebase_DBManager.Action<Long> action);

    // Rider methods
    void addRide(final Ride Ride, final Firebase_DBManager.Action<Long> action);

    void removeRide(long phone, final Firebase_DBManager.Action<Long> action);

    void updateRide(final Ride toUpdate, final Firebase_DBManager.Action<Long> action);


}
