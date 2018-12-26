package model.model.entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * a ride class allows a new user of the app to request a ride via database
 * needs the location of the user
 */
public class Ride {

    public enum ClientRequestStatus {WAITING, HANDLING, CLOSED}

    // ************ fields **************
    private String name;
    private String destination;
    private String location;
    private String phone;
    private String email;
    private ClientRequestStatus status;

    private Date startTime;
    private Date finishTime;
    private String driverName;

    /**
     * constructor
     *
     * @param destination
     * @param loca
     * @param phone
     * @param email
     */
    public Ride(String name, String destination, String loca, String phone, String email) {
        this.name = name;
        this.destination = destination;
        this.location = loca;
        this.phone = phone;
        this.email = email;

        // default values will only be used by reading from the data base
        status = ClientRequestStatus.WAITING;
        startTime = null;
        finishTime = null;
        driverName = null;
    }

    public Ride(){}

    // *********** getters & setters ************

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets destination
     *
     * @return String
     */
    public String getDestination() {
        return destination;
    }

    /**
     * sets the destination
     *
     * @param destination
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * gets a location of the phone
     *
     * @return String
     */
    public String getLocation() {
        return location;
    }

    /**
     * sets the location
     *
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * gets the phone number
     *
     * @return Long
     */
    public String getPhone() {
        return phone;
    }

    /**
     * sets the phone number
     *
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * gets the email
     *
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the email
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public ClientRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ClientRequestStatus status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
