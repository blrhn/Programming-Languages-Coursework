package retentionbasin.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import retentionbasin.service.RetentionBasin;

import java.rmi.RemoteException;

public class RetentionBasinSetupController {
    @FXML private TextField controlCenterNameTextField;
    @FXML private TextField retentionBasinNameTextField;
    @FXML private VBox riverSectionsVBox;
    @FXML private TextField volumeTexField;
    @FXML private Button createButton;
    @FXML private Button connectButton;
    @FXML private TextField tailorHost;
    @FXML private TextField tailorPort;
    @FXML private Button unregisterButton;

    protected RetentionBasin server;
    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {
        retentionBasinNameTextField.setText("Zbiornik");
        tailorHost.setText("localhost");
        tailorPort.setText("2000");
        volumeTexField.setText("85");
        controlCenterNameTextField.setText("Centrala1");
        unregisterButton.setDisable(true);
        riverSectionsVBox.setSpacing(10);

    }

    // dynamically creates user input fields to connect to a river section after clicking the button
    @FXML
    void connectToRiverSection(ActionEvent event) {
        unregisterButton.setDisable(true);
        HBox riverSectionsHBox = new HBox();
        riverSectionsHBox.setAlignment(Pos.CENTER);
        riverSectionsHBox.setSpacing(10);
        riverSectionsHBox.getChildren().add(new TextField("Odcinek rzeczny"));
        Button setButton = new Button("Ustaw");
        riverSectionsHBox.getChildren().add(setButton);
        riverSectionsVBox.getChildren().add(riverSectionsHBox);

        setButton.setOnAction(e -> {
            HBox parent = (HBox) setButton.getParent();
            TextField name = (TextField) parent.getChildren().get(0);
            if (server != null) {
                try {
                    server.connect(name.getText());
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                setButton.setDisable(true);
            }
        });
    }

    @FXML
    void connectToControlCenter(ActionEvent event) throws RemoteException {
        String controlCenterName = controlCenterNameTextField.getText();
        server.connect(controlCenterName);
        connectButton.setDisable(true);
        unregisterButton.setDisable(true);
    }

    // sets retention basin up and registers it to the tailor after clicking the button
    @FXML
    void setRetentionBasin(ActionEvent event) throws RemoteException {
        String name = retentionBasinNameTextField.getText();
        int volume = Integer.parseInt(volumeTexField.getText());
        String host = tailorHost.getText();
        int port = Integer.parseInt(tailorPort.getText());
        server = new RetentionBasin(volume, retentionBasinNameTextField.getText(), mainController, host, port);

        if (server.register(name, volume)) {
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

    // alerts the user if a problem occurs during retention basin creation
    private void printOutAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Niepowodzenie");
        alert.setHeaderText(text);
        alert.showAndWait();
    }
}
