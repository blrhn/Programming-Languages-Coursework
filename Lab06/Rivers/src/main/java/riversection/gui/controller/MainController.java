package riversection.gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import utils.interfaces.Updatable;

public class MainController implements Updatable {

    @FXML
    private RiverSectionSetupController riverSectionSetupViewController;
    @FXML
    private SimulationController simulationViewController;

    public void initialize() {
        riverSectionSetupViewController.injectMainController(this);
        simulationViewController.injectRiverSectionSetupController(riverSectionSetupViewController);
    }

    @Override
    public void update() {
        Platform.runLater(() -> simulationViewController.refresh());
    }
}