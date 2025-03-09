module Rivers {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens controlcenter.gui.controller to javafx.graphics, javafx.fxml;
    opens controlcenter.gui to javafx.graphics, javafx.fxml;
    opens environment.gui.controller to javafx.graphics, javafx.fxml;
    opens environment.gui to javafx.graphics, javafx.fxml;
    opens retentionbasin.gui.controller to javafx.graphics, javafx.fxml;
    opens retentionbasin.gui to javafx.graphics, javafx.fxml;
    opens riversection.gui.controller to javafx.graphics, javafx.fxml;
    opens riversection.gui to javafx.graphics, javafx.fxml;

    exports controlcenter.gui.controller to javafx.graphics, javafx.fxml;
    exports controlcenter.gui to javafx.graphics, javafx.fxml;
    exports environment.gui.controller to javafx.graphics, javafx.fxml;
    exports environment.gui to javafx.graphics, javafx.fxml;
    exports retentionbasin.gui.controller to javafx.graphics, javafx.fxml;
    exports retentionbasin.gui to javafx.graphics, javafx.fxml;
    exports riversection.gui.controller to javafx.graphics, javafx.fxml;
    exports riversection.gui to javafx.graphics, javafx.fxml;
}