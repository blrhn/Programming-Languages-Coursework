package controlcenter.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import controlcenter.service.ControlCenterServer;
import utils.interfaces.Updatable;

import java.util.Map;

public class ControlCenterController implements Updatable {
    @FXML private VBox listRetentionBasinsVBox;
    @FXML private TextField portTextField;

    private ControlCenterServer server;
    private int hBoxCounter;

    public void initialize() {
        portTextField.setText("1235");
        hBoxCounter = 0;
        listRetentionBasinsVBox.setSpacing(4);
    }

    // sets control center up
    @FXML
    void setControlCenter(ActionEvent event) {
        int port = Integer.parseInt(portTextField.getText());
        server = new ControlCenterServer(port, this);
        server.start();
        server.scheduleUpdates();
    }

    // dynamically creates fields to display information about active retention basins and provides an input field
    private void updateActiveRetentionBasins() {
        String retentionBasin = server.getControlCenter().getRetentionBasins().getLast();

        HBox retentionBasinsHBox = new HBox();
        retentionBasinsHBox.setAlignment(Pos.CENTER);
        retentionBasinsHBox.setSpacing(10);
        retentionBasinsHBox.getChildren().add(new Label(retentionBasin));
        retentionBasinsHBox.getChildren().addAll(new Label("Aktualny zrzut wody:"), new Label("0"));
        Button setButton = new Button("Ustaw");
        retentionBasinsHBox.getChildren().addAll(new Label("Ustaw zrzut wody:"), new TextField(), setButton);
        retentionBasinsHBox.getChildren().addAll(new Label("Zapełnienie zbiornika:"), new Label("0"));

        listRetentionBasinsVBox.getChildren().add(hBoxCounter, retentionBasinsHBox);
        hBoxCounter++;

        setButton.setOnAction(event -> {
            HBox parent = (HBox) setButton.getParent();
            int index = listRetentionBasinsVBox.getChildren().indexOf(parent);
            TextField data = (TextField) parent.getChildren().get(4);
            server.setWaterDischarge(Integer.parseInt(data.getText()), index);
        });
    }

    // updates active retention basins' data
    private void updateStatus() {
        Map<Integer, String> waterDischarge = server.getControlCenter().getWaterDischarge();
        Map<Integer, String> fillingPercentage = server.getControlCenter().getFillingPercentage();
        for (int i = 0; i < hBoxCounter; i++) {
            HBox parent = (HBox) listRetentionBasinsVBox.getChildren().get(i);
            Label dischargeLabel = (Label) parent.getChildren().get(2);
            Label fillingPercentageLabel = (Label) parent.getChildren().get(7);

            dischargeLabel.setText(waterDischarge.get(i) + " m3/s");
            fillingPercentageLabel.setText(fillingPercentage.get(i) + " %");
        }
    }

    private void refresh() {
        if (hBoxCounter < server.getControlCenter().getRetentionBasins().size()) {
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
