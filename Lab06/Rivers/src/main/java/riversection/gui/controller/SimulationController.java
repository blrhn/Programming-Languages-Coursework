package riversection.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SimulationController {
    @FXML private Label waterLabel;
    private RiverSectionSetupController setupController;

    public void injectRiverSectionSetupController(RiverSectionSetupController riverSectionSetupController) {
        this.setupController = riverSectionSetupController;
    }

    // simulates the river flow
    private void moveWater() {
        int[] water = setupController.server.getRiverSection().getWater();
        StringBuilder waterString = new StringBuilder();

        for (int i : water) {
            if (i == 0) {
                waterString.append(" . ");
            } else {
                waterString.append(" ").append(i).append(" ");
            }
        }

        waterLabel.setText(waterString.toString());
    }

    public void refresh() {
        moveWater();
    }
}
