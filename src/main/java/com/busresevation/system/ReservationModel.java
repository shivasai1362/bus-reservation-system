
package com.busresevation.system;

public class ReservationModel {
    private String passengerName;
    private String busNumber;
    private String date;
    private int seats;

    public ReservationModel(String passengerName, String busNumber, String date, int seats) {
        this.passengerName = passengerName;
        this.busNumber = busNumber;
        this.date = date;
        this.seats = seats;
    }

    // Getters and setters
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
}