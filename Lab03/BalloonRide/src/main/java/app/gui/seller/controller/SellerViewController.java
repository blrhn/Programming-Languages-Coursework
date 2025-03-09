package app.gui.seller.controller;

import app.gui.shared.utils.SceneSwitcher;
import app.gui.shared.utils.enums.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.model.Seller;

import java.io.IOException;

public class SellerViewController {
    @FXML private Button createButton;

    private Seller seller;
    private final User user = User.SELLER;

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    @FXML
    void goApproveOrders(MouseEvent event) throws IOException {
        Stage stage = (Stage) createButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/seller/gui/seller-activity-view.fxml",
                controller -> {((SellerActivityViewController) controller).approveOrders(seller);}, user.toString());

    }

    @FXML
    void goCreateOffer(MouseEvent event) throws IOException {
        Stage stage = (Stage) createButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/seller/gui/seller-activity-view.fxml",
                controller -> {((SellerActivityViewController) controller).createOffer(seller);}, user.toString());

    }

    @FXML
    void goMarkAsCompleted(MouseEvent event) throws IOException {
        Stage stage = (Stage) createButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/seller/gui/seller-activity-view.fxml",
                controller -> {((SellerActivityViewController) controller).markOrdersAsCompleted(seller);}, user.toString());
    }

    @FXML
    void goProcessOrders(MouseEvent event) throws IOException {
        Stage stage = (Stage) createButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/seller/gui/seller-activity-view.fxml",
                controller -> {((SellerActivityViewController) controller).processOrders(seller);}, user.toString());
    }
}
