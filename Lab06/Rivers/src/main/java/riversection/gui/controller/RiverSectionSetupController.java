package riversection.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import riversection.service.RiverSectionServer;

public class RiverSectionSetupController {
    @FXML private TextField delayFieldText;
    @FXML private TextField environmentHostTexField;
    @FXML private TextField environmentPortTextField;
    @FXML private TextField retentionBasinHost;
    @FXML private TextField retentionBasinPort;
    @FXML private TextField riverSectionHostTextField;
    @FXML private TextField riverSectionPortTextField;

    protected RiverSectionServer server;
    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {
        riverSectionHostTextField.setText("localhost");
        riverSectionPortTextField.setText("123");
        environmentHostTexField.setText("localhost");
        environmentPortTextField.setText("1234");
        delayFieldText.setText("5");
        retentionBasinHost.setText("localhost");
        retentionBasinPort.setText("123");
    }

    @FXML
    void connectToEnvironment(ActionEvent event) {
        String host = environmentHostTexField.getText();
        int port = Integer.parseInt(environmentPortTextField.getText());

        if (server != null) {
            server.assignRiverSection(host, port);
        }
    }

    @FXML
    void connectToRetentionBasin(ActionEvent event) {
        String host = retentionBasinHost.getText();
        int port = Integer.parseInt(retentionBasinPort.getText());

        if (server != null) {
            server.assignRetentionBasin(host, port);
        }
    }

    // sets river section up
    @FXML
    void setRiverSection(ActionEvent event) {
        String host = riverSectionHostTextField.getText();
        int  port = Integer.parseInt(riverSectionPortTextField.getText());
        int delay = Integer.parseInt(delayFieldText.getText());
        server = new RiverSectionServer(host, port, delay, mainController);
        server.start();
    }
}
