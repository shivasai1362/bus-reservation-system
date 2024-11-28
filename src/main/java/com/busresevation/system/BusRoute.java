package com.busresevation.system;

public class BusRoute {
    private String busNumber;
    private String busName;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double fare;
    private int totalSeats;
    private int availableSeats;

    public BusRoute(String busNumber, String busName, String source, String destination,
                   String departureTime, String arrivalTime, double fare, 
                   int totalSeats, int availableSeats) {
        this.busNumber = busNumber;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    // Getters
    public String getBusNumber() { return busNumber; }
    public String getBusName() { return busName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public double getFare() { return fare; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
}