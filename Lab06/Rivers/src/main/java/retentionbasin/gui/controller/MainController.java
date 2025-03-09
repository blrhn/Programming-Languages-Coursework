package retentionbasin.gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import utils.interfaces.Updatable;

public class MainController implements Updatable  {

    @FXML private RetentionBasinSetupController retentionBasinSetupViewController;
    @FXML private SimulationController simulationViewController;

    public void initialize() {
        retentionBasinSetupViewController.injectMainController(this);
        simulationViewController.injectRetentionBasinSetupController(retentionBasinSetupViewController);
    }

    @Override
    public void update() {
        Platform.runLater(() -> simulationViewController.refresh());
    }
}
