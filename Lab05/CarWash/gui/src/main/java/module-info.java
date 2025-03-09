module gui {
    requires simulation;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    exports pwr.edu.carwash.gui;
    exports pwr.edu.carwash.gui.controller;

    opens pwr.edu.carwash.gui to javafx.fxml;
    opens pwr.edu.carwash.gui.controller to javafx.fxml;
}