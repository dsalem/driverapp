package model.model.backend;

import java.util.List;

import com.google.firebase.database.*;

import model.model.datasource.Firebase_DBManager;
import model.model.entities.Driver;
import model.model.entities.Ride;

/**
 * <h1> Connects the application to a Database </h1>
 *  This interface lists all the methods needed for our program to interact with an active Database.
 *  The Database holds two lists one holding all the Drivers , and the other holds all the rides.
 *
 * @author David Salem
 * @author Asher Alexander
 * @version 1.0
 * @since 2019-01-16
 */
public interface Backend {

    // Driver methods

    /**
     * This method allows to add a Driver to the DB.
     *
     * @param Driver holds all the driver details that will be saved.
     * @param action holds what to do if the saving to the DB succeeds or fails.
     */
    void addDriver(final Driver Driver, final Action action);

    /**
     * This method checks if the driver is in the DB inorder to make sure he does not already exist.
     * @param driver the driver we want to add.
     * @param action in case he does exist we will execute onFailure
     */
    void isDriverInDataBase(final Driver driver, final Action action);

    /**
     * This method authenticates the driver using an email and password inorder to login
     * @param dEmail drivers email address.
     * @param dPassword drivers password for the app.
     * @param action holds what to do if the saving to the DB succeeds or fails.
     */
    void isDriversPasswordCorrect(String dEmail ,String dPassword, final Action action);

    /**
     * This method searches for a specific driver in the DB given his email and password.
     * @param email drivers email address.
     * @param password drivers password for the app.
     * @return Driver with the corresponding email and password.
     */
    Driver getDriver(String email ,String password);

    /**
     * This method calculates the total amount of kilometers that a specific diver has driven.
     * @param driver Driver that the calculation will be done to.
     * @return int a number representing the amount of kilometers driven
     */
    int totalKmsForDriver(Driver driver);

    /**
     * This method calculates the total kilometers of a driver for every day of the current month.
     * @param driver Driver that the calculation will be done to.
     * @return an array of integers.
     */
    int[] getMonthlyKms(Driver driver);

    /**
     * This method calculates the total monthly earnings of a driver.
     * @param driver Driver that the calculation will be done to.
     * @return int total monthly earnings.
     */
    int getMonthlyEarnings(Driver driver);

    // Rides methods

    /**
     * This method allows to add a ride request to the DB.
     * @param Ride details of ride.
     * @param action holds what to do if the saving to the DB succeeds or fails.
     */
    void addRide(final Ride Ride, final Action action);

    /**
     * This method allows to update the ride to the DB.
     * @param toUpdate a ride object we wish to update.
     * @param action holds what to do if the saving to the DB succeeds or fails.
     */
    void updateRide(final Ride toUpdate, final Action action);

    /**
     * This method searches for all the rides the a specific driver had taken.
     * @param driver the driver for which the ride history will be searched.
     * @return List of rides that the driver took.
     */
    List<Ride> getDriverHistoryList(Driver driver);

    /**
     * This method gets all the unhandled rides.
     * @return List of all the rides with the status WAITING.
     */
    List<Ride> getWaitingList();

    /**
     * This method searches for a ride with given id used for finding the ride that is being handled in mapActivity.
     * @param id of the person that requested this ride.
     * @return Ride
     */
    Ride getRider(String id);

    /**
     * This method activates a listener to the lists in the DB to check when they are changed.
     * @param notifyDataChange
     */
    void notifyToRideList(final NotifyDataChange<List<Ride>> notifyDataChange);

    /**
     * This interface is taken from the OSF and needed for functions the have different actions when succeeds or fails.
     * @param <T> type of input if needed.
     */
    interface Action<T> {
        void onSuccess();

        void onFailure(Exception exception);

        void onProgress(String status, double percent);
    }

    /**
     * This is an interface that is used to refresh the list.
     * @param <T> type of input if needed
     */
    interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }
}