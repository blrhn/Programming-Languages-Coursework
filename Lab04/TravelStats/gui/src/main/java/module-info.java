module gui {
    requires client;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    exports pwr.edu.travels.gui;
    exports pwr.edu.travels.gui.controller;

    opens pwr.edu.travels.gui to javafx.fxml;
    opens pwr.edu.travels.gui.controller to javafx.fxml;
}