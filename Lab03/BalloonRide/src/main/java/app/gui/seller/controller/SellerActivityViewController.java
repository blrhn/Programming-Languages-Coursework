package app.gui.seller.controller;

import app.gui.shared.utils.SceneSwitcher;
import app.gui.shared.utils.ShowAlert;
import app.gui.shared.utils.enums.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.dao.OfferDao;
import service.dao.OrderDao;
import service.impl.OfferDaoImpl;
import service.impl.OrderDaoImpl;
import service.model.Offer;
import service.model.Order;
import service.model.Seller;

import java.io.IOException;
import java.util.List;

public class SellerActivityViewController {
    @FXML private Button confirmButton;
    @FXML private GridPane gridPane;
    @FXML private ComboBox<Integer> idComboBox;
    @FXML private Text text;
    @FXML private TextField offerTitleInput;

    private final TableView<Order> orderTable = new TableView<>();

    private static final OfferDao offerDao = new OfferDaoImpl();
    private static final OrderDao orderDao = new OrderDaoImpl();

    private static final String[] status = {"Oczekujace", "Zatwierdzone", "Przyjete do realizacji", "Zrealizowane"};
    private Seller localSeller;
    private final User user = User.SELLER;

    public void initialize() {
        offerTitleInput.setVisible(false);
    }

    private void createOrderTableView(ObservableList<Order> orderList, boolean showTitle) {
        orderTable.getItems().clear();

        TableColumn<Order, Integer> orderId = new TableColumn<>("Id");
        orderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Order, Integer> clientId = new TableColumn<>("Id klienta");
        clientId.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        TableColumn<Order, Integer> offerId = new TableColumn<>("Id oferty");
        offerId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getOffer().getId()).asObject());

        orderTable.getColumns().add(orderId);
        orderTable.getColumns().add(clientId);
        orderTable.getColumns().add(offerId);

        if (showTitle) {
            TableColumn<Order, String> name = new TableColumn<>("Nazwa");
            name.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getOffer().getTitle()));

            orderTable.getColumns().add(name);

        }

        orderTable.setItems(orderList);

        gridPane.add(orderTable, 0, 0);
    }

    private void populateComboBoxOrderId(List<Order> offers) {
        List<Integer> ids = offers.stream().map(Order::getId).toList();

        idComboBox.getItems().addAll(ids);
    }

    private void hideUserInput() {
        confirmButton.setVisible(false);
        idComboBox.setVisible(false);
        text.setVisible(false);
    }

    protected void createOffer(Seller seller) {
        localSeller = seller;
        offerTitleInput.setVisible(true);
        text.setVisible(false);
        idComboBox.setVisible(false);

        confirmButton.setOnAction(event -> {
            offerDao.addOffer(new Offer(offerTitleInput.getText()));

            ShowAlert.showAlert("Utworzono ofertę");
        });
    }

    protected void approveOrders(Seller seller) {
        localSeller = seller;
        List<Order> nonApprovedOrders = orderDao.getNonApprovedOrders();

        if (nonApprovedOrders.isEmpty()) {
            hideUserInput();
            ShowAlert.showAlert("Brak zamowien oczekujacych na zatwierdzenie");
            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(nonApprovedOrders);
        createOrderTableView(ordersObservable, false);
        text.setText("Id zamówienia do zatwierdzenia:");
        populateComboBoxOrderId(nonApprovedOrders);

        confirmButton.setOnAction(event -> {
            orderDao.changeStatus(idComboBox.getValue(), status[1], status[0]);
            ShowAlert.showAlert("Zatwierdzono zamówienie");
        });
    }

    protected void processOrders(Seller seller) {
        localSeller = seller;
        List<Order> ordersWithDeclaredClients = orderDao.getOrdersWithDeclaredClients();

        if (ordersWithDeclaredClients.isEmpty()) {
            hideUserInput();
            ShowAlert.showAlert("Brak zamowien z zadeklarowanymi klientami");
            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(ordersWithDeclaredClients);
        createOrderTableView(ordersObservable, true);
        text.setText("Zamówienia do przekazania do realizacji:");
        populateComboBoxOrderId(ordersWithDeclaredClients);

        confirmButton.setOnAction(event -> {
            orderDao.changeStatus(idComboBox.getValue(), status[2], status[1]);
            ShowAlert.showAlert("Przekazano do realizacji");
        });
    }

    protected void markOrdersAsCompleted(Seller seller) {
        localSeller = seller;
        List<Order> finalisedOrders = orderDao.getFinalisedOrders();

        if (finalisedOrders.isEmpty()) {
            hideUserInput();
            ShowAlert.showAlert("Brak zamowien do oznaczenia jako zrealizowane");
            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(finalisedOrders);
        createOrderTableView(ordersObservable, true);
        text.setText("Id zrealizowanego zamówienia:");
        populateComboBoxOrderId(finalisedOrders);

        confirmButton.setOnAction(event -> {
            orderDao.changeStatus(idComboBox.getValue(), status[3], status[2]);
            ShowAlert.showAlert("Zrealizowano zamówienie");
        });
    }

    @FXML
    void goBack(MouseEvent event) throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/seller/gui/seller-main-view.fxml",
                controller -> ((SellerViewController) controller).setSeller(localSeller), user.toString());
    }
}
