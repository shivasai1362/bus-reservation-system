package com.busresevation.system;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> busNumberCombo;
    @FXML private TextField dateField;
    @FXML private TextField seatsField;
    @FXML private Button reserveButton;
    @FXML private Button showReservationsButton;
    @FXML private Button showAvailableBusesButton;

    private DatabaseService databaseService;

    @FXML
    public void initialize() {
        databaseService = new DatabaseService();
        try {
            databaseService.initializeDatabase();
            busNumberCombo.getItems().addAll(databaseService.getBusNumbers());
        } catch (Exception e) {
            showAlert("Database Error", "Failed to initialize database");
        }

        reserveButton.setOnAction(e -> handleReservation());
        showReservationsButton.setOnAction(e -> showReservations());
        showAvailableBusesButton.setOnAction(e -> showAvailableBuses());
    }

    private void handleReservation() {
        if (validateInput()) {
            ReservationModel reservation = new ReservationModel(
                nameField.getText(),
                busNumberCombo.getValue(),
                dateField.getText(),
                Integer.parseInt(seatsField.getText())
            );

            if (databaseService.makeReservation(reservation)) {
                showAlert("Success", "Reservation completed successfully!");
                clearFields();
            } else {
                showAlert("Error", "Failed to make reservation");
            }
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || busNumberCombo.getValue() == null ||
            dateField.getText().isEmpty() || seatsField.getText().isEmpty()) {
            showAlert("Validation Error", "All fields are required");
            return false;
        }

        // Validate date
        if (!validateDate()) {
            return false;
        }

        // Validate seats
        try {
            Integer.parseInt(seatsField.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Seats must be a number");
            return false;
        }
        return true;
    }

    private boolean validateDate() {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate enteredDate = LocalDate.parse(dateField.getText(), inputFormatter);
            LocalDate currentDate = LocalDate.now();

            if (enteredDate.isBefore(currentDate) || enteredDate.isEqual(currentDate)) {
                showAlert("Validation Error", "Please enter a future date");
                return false;
            }
            
            // Convert to yyyy-MM-dd format for database storage
            dateField.setText(enteredDate.format(dbFormatter));
            return true;
        } catch (DateTimeParseException e) {
            showAlert("Validation Error", "Invalid date format. Use DD-MM-YYYY");
            return false;
        }
    }

    private void showReservations() {
        try {
            Stage stage = new Stage();
            TableView<ReservationModel> table = new TableView<>();
            
            TableColumn<ReservationModel, String> nameCol = new TableColumn<>("Passenger Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
            
            TableColumn<ReservationModel, String> busCol = new TableColumn<>("Bus Number");
            busCol.setCellValueFactory(new PropertyValueFactory<>("busNumber"));
            
            TableColumn<ReservationModel, String> dateCol = new TableColumn<>("Travel Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            
            TableColumn<ReservationModel, Integer> seatsCol = new TableColumn<>("Seats");
            seatsCol.setCellValueFactory(new PropertyValueFactory<>("seats"));
            
            table.getColumns().addAll(nameCol, busCol, dateCol, seatsCol);
            
            ObservableList<ReservationModel> reservations = FXCollections.observableArrayList(
                databaseService.getAllReservations()
            );
            table.setItems(reservations);
            
            Scene scene = new Scene(table, 600, 400);
            stage.setTitle("All Reservations");
            stage.setScene(scene);
            stage.show();
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch reservations");
            e.printStackTrace();
        }
    }

    private void showAvailableBuses() {
        try {
            Stage stage = new Stage();
            TableView<BusRoute> table = new TableView<>();
            
            TableColumn<BusRoute, String> numberCol = new TableColumn<>("Bus Number");
            numberCol.setCellValueFactory(new PropertyValueFactory<>("busNumber"));
            
            TableColumn<BusRoute, String> nameCol = new TableColumn<>("Bus Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("busName"));
            
            TableColumn<BusRoute, String> sourceCol = new TableColumn<>("Source");
            sourceCol.setCellValueFactory(new PropertyValueFactory<>("source"));
            
            TableColumn<BusRoute, String> destCol = new TableColumn<>("Destination");
            destCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
            
            TableColumn<BusRoute, String> depCol = new TableColumn<>("Departure");
            depCol.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
            
            TableColumn<BusRoute, String> arrCol = new TableColumn<>("Arrival");
            arrCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
            
            TableColumn<BusRoute, Double> fareCol = new TableColumn<>("Fare");
            fareCol.setCellValueFactory(new PropertyValueFactory<>("fare"));
            
            TableColumn<BusRoute, Integer> availCol = new TableColumn<>("Available Seats");
            availCol.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
            
            table.getColumns().addAll(numberCol, nameCol, sourceCol, destCol, 
                                    depCol, arrCol, fareCol, availCol);
            
            ObservableList<BusRoute> buses = FXCollections.observableArrayList();
            ResultSet rs = databaseService.getAvailableBuses();
            while (rs.next()) {
                buses.add(new BusRoute(
                    rs.getString("bus_number"),
                    rs.getString("bus_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getTime("departure_time").toString(),
                    rs.getTime("arrival_time").toString(),
                    rs.getDouble("fare"),
                    rs.getInt("total_seats"),
                    rs.getInt("available_seats")
                ));
            }
            
            table.setItems(buses);
            
            Scene scene = new Scene(table, 800, 400); // Made wider to fit all columns
            stage.setTitle("Available Buses");
            stage.setScene(scene);
            stage.show();
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch available buses");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        busNumberCombo.setValue(null);
        dateField.clear();
        seatsField.clear();
    }
}