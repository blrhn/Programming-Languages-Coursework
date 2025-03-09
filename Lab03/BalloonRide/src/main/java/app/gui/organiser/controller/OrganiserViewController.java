package app.gui.organiser.controller;

import app.gui.shared.utils.SceneSwitcher;
import app.gui.shared.utils.enums.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.model.Organiser;

import java.io.IOException;

public class OrganiserViewController {
    @FXML private Button updateOrders;

    private Organiser organiser;
    private final User user = User.ORGANISER;


    public void setOrganiser(Organiser organiser) {
        this.organiser = organiser;
    }

    @FXML
    void goApproveClients(MouseEvent event) throws IOException {
        Stage stage = (Stage) updateOrders.getScene().getWindow();
        SceneSwitcher.switchScene(stage,"/organiser/gui/organiser-activity-view.fxml",
                controller -> {((OrganiserActivityViewController) controller).approveClients(organiser);}, user.toString());
    }

    @FXML
    void goMarkAsFinalised(MouseEvent event) throws IOException {
        Stage stage = (Stage) updateOrders.getScene().getWindow();
        SceneSwitcher.switchScene(stage,"/organiser/gui/organiser-activity-view.fxml",
                controller -> {((OrganiserActivityViewController) controller).markAsFinalised(organiser);}, user.toString());
    }

    @FXML
    void goUpdateOrders(MouseEvent event) throws IOException {
        Stage stage = (Stage) updateOrders.getScene().getWindow();
        SceneSwitcher.switchScene(stage,"/organiser/gui/organiser-activity-view.fxml",
                controller -> {((OrganiserActivityViewController) controller).browseAndUpdateOrders(organiser);}, user.toString());

    }
}
