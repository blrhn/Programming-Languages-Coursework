module BalloonRide {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports app.gui.shared;
    exports app.gui.client;
    exports app.gui.client.controller;
    exports app.gui.organiser;
    exports app.gui.organiser.controller;
    exports app.gui.seller;
    exports app.gui.seller.controller;

    exports service.model;

    opens app.gui.shared to javafx.fxml;
    opens app.gui.seller to javafx.fxml;
    opens app.gui.seller.controller to javafx.fxml;
    opens app.gui.client to javafx.fxml;
    opens app.gui.client.controller to javafx.fxml;
    opens app.gui.organiser to javafx.fxml;
    opens app.gui.organiser.controller to javafx.fxml;

    opens service.model to javafx.fxml;
}