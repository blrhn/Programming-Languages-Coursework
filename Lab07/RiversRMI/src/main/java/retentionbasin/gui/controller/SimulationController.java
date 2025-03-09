package retentionbasin.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.NavigableMap;
import java.util.TreeMap;

public class SimulationController {
    @FXML private Rectangle retentionBasinRectangle;

    private final NavigableMap<Long, Integer> visualisationMap = new TreeMap<>();
    private RetentionBasinSetupController retentionBasinSetupController;

    public void injectRetentionBasinSetupController(RetentionBasinSetupController retentionBasinSetupController) {
        this.retentionBasinSetupController = retentionBasinSetupController;
    }

    public void initialize() {
        visualisationMap.put(0L, 255);
        visualisationMap.put(1L, 230);

        int t = 25;

        for (long i = 11; i < 92; i += 10) {
            visualisationMap.put(i, 230 - t);
            t += 25;
        }
    }

    // simulates filling the retention basin up
    private void updateFilledStatus() {
        long percentage = retentionBasinSetupController.server.getFillingPercentage();
        int value = visualisationMap.floorEntry(percentage).getValue();
        Color newColour = Color.rgb(value, value, 255);
        retentionBasinRectangle.setFill(newColour);
    }

    public void refresh() {
        updateFilledStatus();
    }
}
