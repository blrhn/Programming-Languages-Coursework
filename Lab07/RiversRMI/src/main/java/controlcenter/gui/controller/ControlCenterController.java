package controlcenter.gui.controller;

import controlcenter.service.ControlCenter;
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
import java.util.Map;

public class ControlCenterController implements Updatable {
    @FXML private VBox listRetentionBasinsVBox;
    @FXML private TextField nameTextField;
    @FXML private Button setButton;
    @FXML private TextField tailorHost;
    @FXML private TextField tailorPort;
    @FXML private Button unregisterButton;

    private ControlCenter server;
    private int hBoxCounter;

    public void initialize() {
        nameTextField.setText("Centrala1");
        tailorHost.setText("localhost");
        tailorPort.setText("2000");
        hBoxCounter = 0;
        listRetentionBasinsVBox.setSpacing(4);
        unregisterButton.setDisable(true);
    }

    // sets control center up and registers it to the tailor after clicking the button
    @FXML
    void setControlCenter(ActionEvent event) throws RemoteException {
        String name = nameTextField.getText();
        String host = tailorHost.getText();
        int port = Integer.parseInt(tailorPort.getText());
        server = new ControlCenter(this, host, port);
        if (server.register(name)) {
            server.scheduleUpdates();
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
        listRetentionBasinsVBox.getChildren().clear();
        setButton.setDisable(false);
    }

    // dynamically creates fields to display information about active retention basins and provides an input field
    private void updateActiveRetentionBasins() {
        int size = server.getRetentionBasins().size();
        String retentionBasin = server.getRetentionBasins().get(size - 1);

        HBox retentionBasinsHBox = new HBox();
        retentionBasinsHBox.setAlignment(Pos.CENTER);

        if (hBoxCounter == 0) {
            retentionBasinsHBox.setSpacing(10);
        } else {
            retentionBasinsHBox.setSpacing(8);
        }

        retentionBasinsHBox.getChildren().add(new Label(retentionBasin));
        retentionBasinsHBox.getChildren().addAll(new Label("Aktualny zrzut wody:"), new Label("0"));
        Button setButton = new Button("Ustaw");
        retentionBasinsHBox.getChildren().addAll(new Label("Ustaw zrzut wody:"), new TextField(), setButton);
        retentionBasinsHBox.getChildren().addAll(new Label("Zapełnienie zbiornika:"), new Label("0"));

        listRetentionBasinsVBox.getChildren().add(hBoxCounter, retentionBasinsHBox);
        hBoxCounter++;

        setButton.setOnAction(event -> {
            HBox parent = (HBox) setButton.getParent();
            Label name = (Label) parent.getChildren().get(0);
            TextField discharge = (TextField) parent.getChildren().get(4);
            try {
                server.sendDischarge(name.getText(), Integer.parseInt(discharge.getText()));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });
    }

    // updates active retention basins' data
    private void updateStatus() {
        Map<String, String> basinInfo = server.getBasinInfo();
        int i = 0;
        for (String name : basinInfo.keySet()) {
            String info = basinInfo.get(name);
            String discharge = info.split(":")[0];
            String percentage = info.split(":")[1];

            HBox parent = (HBox) listRetentionBasinsVBox.getChildren().get(i);
            Label dischargeLabel = (Label) parent.getChildren().get(2);
            Label fillingPercentageLabel = (Label) parent.getChildren().get(7);

            dischargeLabel.setText(discharge + " [m3/s]");
            fillingPercentageLabel.setText(percentage + " [%]");
            i++;
        }
    }

    private void refresh() {
        if (hBoxCounter < server.getRetentionBasins().size()) {
            updateActiveRetentionBasins();
        }
        updateStatus();
    }

    // updates gui
    @Override
    public void update() {
        Platform.runLater(this::refresh);
    }
}