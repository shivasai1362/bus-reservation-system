package com.busresevation.system;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String URL = "jdbc:postgresql://localhost:5432/bus_reservation";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String createReservationsTable = "CREATE TABLE IF NOT EXISTS reservations ("
                + "id SERIAL PRIMARY KEY,"
                + "passenger_name VARCHAR(100),"
                + "bus_number VARCHAR(20),"
                + "travel_date DATE,"
                + "seats INTEGER"
                + ")";
            
            conn.createStatement().execute(createReservationsTable);
        }
    }

    private boolean tableHasData(String tableName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1");
            return rs.next();
        }
    }

    public boolean makeReservation(ReservationModel reservation) {
        String sql = "INSERT INTO reservations (passenger_name, bus_number, travel_date, seats) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getPassengerName());
            pstmt.setString(2, reservation.getBusNumber());
            pstmt.setDate(3, Date.valueOf(reservation.getDate()));
            pstmt.setInt(4, reservation.getSeats());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ReservationModel> getAllReservations() throws SQLException {
        String sql = "SELECT passenger_name, bus_number, travel_date, seats FROM reservations "
            + "ORDER BY travel_date, bus_number";
        List<ReservationModel> reservations = new ArrayList<>();
            
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(new ReservationModel(
                    rs.getString("passenger_name"),
                    rs.getString("bus_number"),
                    rs.getDate("travel_date").toString(),
                    rs.getInt("seats")
                ));
            }
        }
        return reservations;
    }

    public ResultSet getAvailableBuses() throws SQLException {
        String sql = "SELECT bi.bus_number, bi.bus_name, bi.source, bi.destination, "
            + "bi.departure_time, bi.arrival_time, bi.fare, bi.total_seats, "
            + "(bi.total_seats - COALESCE((SELECT SUM(seats) FROM reservations r "
            + "WHERE r.bus_number = bi.bus_number AND r.travel_date = CURRENT_DATE), 0)) as available_seats "
            + "FROM bus_info bi "
            + "ORDER BY bi.departure_time";
        
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        return conn.createStatement().executeQuery(sql);
    }

    public List<String> getBusNumbers() throws SQLException {
        String sql = "SELECT bus_number FROM bus_info";
        List<String> busNumbers = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                busNumbers.add(rs.getString("bus_number"));
            }
        }
        return busNumbers;
    }
}