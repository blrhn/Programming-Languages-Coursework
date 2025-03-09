package app.gui.shared.utils;

import javafx.scene.control.Alert;

public class ShowAlert {
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("INFORMACJA");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
