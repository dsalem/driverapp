package model.model.entities;

import java.sql.Time;

public class Ride {
    public enum Status {WAITING,HANDLING,CLOSED}
    private String startLocation;
    private String destinationLocation;
    private Time startTime;
    private Time endTime;
    private String passengerName;
    private Long passengerPhoneNumber;
    private String passengerEmail;

    public Ride(String startLocation, String destinationLocation, Time startTime, Time endTime, String passengerName, Long passengerPhoneNumber, String passengerEmail) {
        this.startLocation = startLocation;
        this.destinationLocation = destinationLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.passengerName = passengerName;
        this.passengerPhoneNumber = passengerPhoneNumber;
        this.passengerEmail = passengerEmail;
    }

    public Ride(){}

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Long getPassengerPhoneNumber() {
        return passengerPhoneNumber;
    }

    public void setPassengerPhoneNumber(Long passengerPhoneNumber) {
        this.passengerPhoneNumber = passengerPhoneNumber;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }
}
