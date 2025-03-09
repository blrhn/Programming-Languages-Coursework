package environment.gui.controller;

import environment.service.Environment;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.Updatable;

import java.rmi.RemoteException;

public class EnvironmentController implements Updatable {
    @FXML private VBox listRiverSectionsVBox;
    @FXML private TextField nameTextField;
    @FXML private Button setButton;
    @FXML private TextField tailorHost;
    @FXML private TextField tailorPort;
    @FXML private Button unregisterButton;

    private Environment server;
    private int hBoxCounter;

    public void initialize() {
        nameTextField.setText("Środowisko1");
        tailorHost.setText("localhost");
        tailorPort.setText("2000");
        hBoxCounter = 0;
        listRiverSectionsVBox.setSpacing(10);
        unregisterButton.setDisable(true);
    }

    // sets environment up and registers it to the tailor after clicking the button
    @FXML
    void setEnvironment(ActionEvent event) throws RemoteException {
        String name = nameTextField.getText();
        String host = tailorHost.getText();
        int port = Integer.parseInt(tailorPort.getText());
        server = new Environment(this, host, port);

        if (server.register(name)) {
            setButton.setDisable(true);
            unregisterButton.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Niepowodzenie");
            alert.setHeaderText("Obiekt o nazwie " + name + " juz istnieje");
            alert.showAndWait();
        }
    }

    // handles unregister button
    @FXML
    void unregister(ActionEvent event) throws RemoteException {
        server.unregister();
        unregisterButton.setDisable(true);
        listRiverSectionsVBox.getChildren().clear();
        setButton.setDisable(false);
    }

    // dynamically creates input fields for the user to set and send current rainfall for each active river section
    private void updateActiveRiverSections() {
        int size = server.getRivers().size();
        String riverSection = server.getRivers().get(size - 1);

        HBox riverSectionHBox = new HBox();
        riverSectionHBox.setAlignment(Pos.CENTER);

        if (hBoxCounter == 0) {
            riverSectionHBox.setSpacing(10);
        } else {
            riverSectionHBox.setSpacing(7);
        }

        riverSectionHBox.getChildren().add(new Label(riverSection));
        riverSectionHBox.getChildren().addAll(new Label("Wielkość opadów: "), new TextField());
        Button sendButton = new Button("Wyślij");
        riverSectionHBox.getChildren().add(sendButton);

        listRiverSectionsVBox.getChildren().add(hBoxCounter, riverSectionHBox);
        hBoxCounter++;

        sendButton.setOnAction(event -> {
            HBox parent = (HBox) sendButton.getParent();
            Label name = (Label) parent.getChildren().get(0);
            TextField rainfall = (TextField) parent.getChildren().get(2);
            try {
                server.sendRainfall(name.getText(), Integer.parseInt(rainfall.getText()));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void refresh() {
        updateActiveRiverSections();
    }

    @Override
    public void update() {
        Platform.runLater(this::refresh);
    }
}
