package riversection.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import riversection.service.RiverSection;

import java.rmi.RemoteException;

public class RiverSectionSetupController {
    @FXML private TextField delayFieldText;
    @FXML private TextField environmentNameTexField;
    @FXML private TextField retentionBasinName;
    @FXML private TextField riverSectionNameTextField;
    @FXML private Button createButton;
    @FXML private Button connectEnvironmentButton;
    @FXML private Button connectRetentionBasinButton;
    @FXML private TextField tailorHost;
    @FXML private TextField tailorPort;
    @FXML private Button unregisterButton;

    protected RiverSection server;
    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {
        riverSectionNameTextField.setText("Odcinek rzeczny");
        environmentNameTexField.setText("Środowisko1");
        delayFieldText.setText("5");
        retentionBasinName.setText("Zbiornik");
        tailorHost.setText("localhost");
        tailorPort.setText("2000");
        unregisterButton.setDisable(true);
    }

    @FXML
    void connectToEnvironment(ActionEvent event) throws RemoteException {
        server.connect(environmentNameTexField.getText());
        connectEnvironmentButton.setDisable(true);
        unregisterButton.setDisable(true);

    }

    @FXML
    void connectToRetentionBasin(ActionEvent event) throws RemoteException {
        server.connect(retentionBasinName.getText());
        connectRetentionBasinButton.setDisable(true);
        unregisterButton.setDisable(true);
    }

    // sets river section up and registers it to the tailor after clicking the button
    @FXML
    void setRiverSection(ActionEvent event) throws RemoteException {
        String name = riverSectionNameTextField.getText();
        int delay = Integer.parseInt(delayFieldText.getText());
        String host = tailorHost.getText();
        int port = Integer.parseInt(tailorPort.getText());
        server = new RiverSection(name, delay, mainController, host, port);

        if (server.register(name)) {
            createButton.setDisable(true);
            unregisterButton.setDisable(false);
        } else {
            printOutAlert("Obiekt o nazwie " + name + " juz istnieje");
        }
    }

    // handles unregister button
    @FXML
    void unregister(ActionEvent event) throws RemoteException {
        server.unregister();
        unregisterButton.setDisable(true);
        createButton.setDisable(false);
    }

    // alerts the user if a problem occurs during river section creation
    private void printOutAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Niepowodzenie");
        alert.setHeaderText(text);
        alert.showAndWait();
    }
}
