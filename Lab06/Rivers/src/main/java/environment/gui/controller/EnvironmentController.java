package environment.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import environment.service.EnvironmentServer;
import utils.interfaces.Updatable;

public class EnvironmentController implements Updatable {
    @FXML private VBox listRiverSectionsVBox;

    @FXML private TextField portTextField;

    private EnvironmentServer server;
    private int hBoxCounter;

    public void initialize() {
        portTextField.setText("1234");
        hBoxCounter = 0;
        listRiverSectionsVBox.setSpacing(10);
    }

    // sets environment up
    @FXML
    void setEnvironment(ActionEvent event) {
        int port = Integer.parseInt(portTextField.getText());

        server = new EnvironmentServer(port, this);
        server.start();
    }

    // dynamically creates input fields for the user to set and send current rainfall for each active river section
    private void updateActiveRiverSections() {
        String riverSection = server.getEnvironment().getRivers().getLast();

        HBox riverSectionHBox = new HBox();
        riverSectionHBox.setAlignment(Pos.CENTER);
        riverSectionHBox.setSpacing(10);
        riverSectionHBox.getChildren().add(new Label(riverSection));
        riverSectionHBox.getChildren().addAll(new Label("Wielkość opadów: "), new TextField());
        Button sendButton = new Button("Wyślij");
        riverSectionHBox.getChildren().add(sendButton);

        listRiverSectionsVBox.getChildren().add(hBoxCounter, riverSectionHBox);
        hBoxCounter++;

        sendButton.setOnAction(event -> {
            HBox parent = (HBox) sendButton.getParent();
            int index = listRiverSectionsVBox.getChildren().indexOf(parent);
            TextField data = (TextField) parent.getChildren().get(2);
            server.setRainfall(Integer.parseInt(data.getText()), index);
        });
    }

    private void refresh() {
        updateActiveRiverSections();
    }

    // updates gui
    @Override
    public void update() {
        Platform.runLater(this::refresh);
    }
}
