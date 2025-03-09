package app.gui.client.controller;

import app.gui.shared.utils.SceneSwitcher;
import app.gui.shared.utils.enums.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.model.Client;

import java.io.IOException;

public class ClientViewController {
    @FXML private Button browseOffers;

    private Client client;
    private final User user = User.CLIENT;

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    void goBrowseOffers(MouseEvent event) throws IOException {
        Stage stage = (Stage) browseOffers.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/client/gui/client-activity-view.fxml",
                controller -> {((ClientActivityViewController) controller).browseAndOrder(client);}, user.toString());
    }

    @FXML
    void goBrowseOrders(MouseEvent event) throws IOException {
        Stage stage = (Stage) browseOffers.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/client/gui/client-activity-view.fxml",
                controller -> {((ClientActivityViewController) controller).printOrders(client);}, user.toString());
    }

    @FXML
    void goCheckApproval(MouseEvent event) throws IOException {
        Stage stage = (Stage) browseOffers.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/client/gui/client-activity-view.fxml",
                controller -> {((ClientActivityViewController) controller).checkDeclarationStatus(client);}, user.toString());
    }

    @FXML
    void goDeclare(MouseEvent event) throws IOException {
        Stage stage = (Stage) browseOffers.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/client/gui/client-activity-view.fxml",
                controller -> {((ClientActivityViewController) controller).declareParticipation(client);}, user.toString());

    }

    @FXML
    void goDeleteOrders(MouseEvent event) throws IOException {
        Stage stage = (Stage) browseOffers.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/client/gui/client-activity-view.fxml",
                controller -> {((ClientActivityViewController) controller).deleteCompletedOrders(client);}, user.toString());
    }
}
