package tailor.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import tailor.service.Tailor;

public class TailorController {
    @FXML private Button setButton;
    @FXML private TextField tailorPort;

    public void initialize() {
        tailorPort.setText("2000");
    }

    @FXML
    void setTailor(ActionEvent event) {
        int port = Integer.parseInt(tailorPort.getText());
        Tailor tailor = new Tailor(port);
        tailor.initializeTailor();
        setButton.setDisable(true);
    }
}
