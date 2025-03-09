package retentionbasin.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import retentionbasin.service.RetentionBasinServer;

public class RetentionBasinSetupController {
    @FXML private TextField controlCenterHostTexField;
    @FXML private TextField controlCenterPortTextField;
    @FXML private TextField retentionBasinHostTextField;
    @FXML private TextField retentionBasinPortTextField;
    @FXML private VBox riverSectionsVBox;
    @FXML private TextField volumeTexField;

    protected RetentionBasinServer server;

    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {
        retentionBasinHostTextField.setText("localhost");
        retentionBasinPortTextField.setText("123");
        volumeTexField.setText("85");
        controlCenterHostTexField.setText("localhost");
        controlCenterPortTextField.setText("1235");

        riverSectionsVBox.setSpacing(10);
    }

    // dynamically creates user input fields to connect to a river section after clicking the button
    @FXML
    void connectToRiverSection(ActionEvent event) {
        HBox riverSectionsHBox = new HBox();
        riverSectionsHBox.setAlignment(Pos.CENTER);
        riverSectionsHBox.setSpacing(10);
        riverSectionsHBox.getChildren().add(new TextField("localhost"));
        riverSectionsHBox.getChildren().add(new TextField("123"));
        Button setButton = new Button("Ustaw");
        riverSectionsHBox.getChildren().add(setButton);
        riverSectionsVBox.getChildren().add(riverSectionsHBox);

        setButton.setOnAction(e -> {
            HBox parent = (HBox) setButton.getParent();
            TextField host = (TextField) parent.getChildren().getFirst();
            TextField port = (TextField) parent.getChildren().get(1);
            if (server != null) {
                server.assignRetentionBasin(host.getText(), Integer.parseInt(port.getText()));
            }
        });
    }

    @FXML
    void connectToControlCenter(ActionEvent event) {
        String host = controlCenterHostTexField.getText();
        int port = Integer.parseInt(controlCenterPortTextField.getText());

        if (server != null) {
            server.assignRetentionBasin(host, port);
        }
    }

    // sets retention basin up and registers it to the tailor after clicking the button
    @FXML
    void setRetentionBasin(ActionEvent event) {
        String host = retentionBasinHostTextField.getText();
        int port = Integer.parseInt(retentionBasinPortTextField.getText());
        int volume = Integer.parseInt(volumeTexField.getText());

        server = new RetentionBasinServer(host, port, volume, mainController);
        server.start();
    }
}
