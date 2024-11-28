module com.busresevation.system {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.busresevation.system to javafx.fxml;
    exports com.busresevation.system;
}
