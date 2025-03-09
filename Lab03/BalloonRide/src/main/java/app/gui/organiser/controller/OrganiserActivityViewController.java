package app.gui.organiser.controller;

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
import service.dao.OrderDao;
import service.exceptions.AlreadyUpdatedException;
import service.impl.OrderDaoImpl;
import service.model.Order;
import service.model.Organiser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class OrganiserActivityViewController {
    @FXML private Button confirmButton;
    @FXML private GridPane gridPane;
    @FXML private ComboBox<Integer> idComboBox;
    @FXML private Text text;
    @FXML private Text dateText;
    @FXML private TextField dateTextField;

    private final TableView<Order> orderTable = new TableView<>();

    private static final OrderDao orderDao = new OrderDaoImpl();

    private Organiser localOrganiser;
    private final User user = User.ORGANISER;

    public void initialize() {
        dateText.setVisible(false);
        dateTextField.setVisible(false);
    }

    private void createOrderTableView(ObservableList<Order> orderList, boolean showDate) {
        orderTable.getItems().clear();

        TableColumn<Order, Integer> orderId = new TableColumn<>("Id zamówienia");
        orderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Order, Integer> clientId = new TableColumn<>("Id klienta");
        clientId.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        TableColumn<Order, Integer> offerId = new TableColumn<>("Id oferty");
        offerId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getOffer().getId()).asObject());
        TableColumn<Order, String> name = new TableColumn<>("Nazwa");
        name.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOffer().getTitle()));

        orderTable.getColumns().add(orderId);
        orderTable.getColumns().add(clientId);
        orderTable.getColumns().add(offerId);
        orderTable.getColumns().add(name);

        if (showDate) {
            TableColumn<Order, String> date = new TableColumn<>("Data");
            date.setCellValueFactory(new PropertyValueFactory<>("date"));

            orderTable.getColumns().add(date);
        }

        orderTable.setItems(orderList);

        gridPane.add(orderTable, 0, 0);
    }

    private void hideUserInput() {
        confirmButton.setVisible(false);
        idComboBox.setVisible(false);
        text.setVisible(false);
    }

    private void populateComboBoxOrderId(List<Order> offers) {
        List<Integer> ids = offers.stream().map(Order::getId).toList();

        idComboBox.getItems().addAll(ids);
    }

    protected void browseAndUpdateOrders(Organiser organiser) {
        localOrganiser = organiser;
        List<Order> orders = orderDao.getApprovedOrders();

        if (orders.isEmpty()) {
            hideUserInput();
            ShowAlert.showAlert("Brak zamowien do zaktualizowania");

            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(orders);
        createOrderTableView(ordersObservable, false);
        populateComboBoxOrderId(orders);

        text.setText("Id zamowienia do zaktualizowania:");
        dateText.setVisible(true);
        dateTextField.setVisible(true);
        dateText.setText("Data w formacie yyyy-mm-dd:");

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        confirmButton.setOnAction(event -> {
            try {
                LocalDate.parse(dateTextField.getText(), dateTimeFormatter);
                try {
                    orderDao.assureOrderNotTaken(idComboBox.getValue());
                    orderDao.updateOrderDate(idComboBox.getValue(), dateTextField.getText());
                    orderDao.updateOrganiser(idComboBox.getValue(), organiser.getId());

                    ShowAlert.showAlert("Oferta została zaktualizowana");
                } catch (AlreadyUpdatedException aue) {
                    ShowAlert.showAlert("Inny organizator podjal sie realizacji tego zamowienia");
                }
            } catch (DateTimeParseException e) {
                ShowAlert.showAlert("Podano nieprawidlowa date");
            }
        });
    }

    protected void approveClients(Organiser organiser) {
        localOrganiser = organiser;
        List<Order> ordersToComplete = orderDao.getOrdersToComplete(organiser.getId());

        if (ordersToComplete.isEmpty()) {
            hideUserInput();
            ShowAlert.showAlert("Brak klientow do zatwierdzenia");

            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(ordersToComplete);
        createOrderTableView(ordersObservable, true);
        text.setText("Zamówienie, którego klienta zatwierdzasz:");
        populateComboBoxOrderId(ordersToComplete);

        confirmButton.setOnAction(event -> {
            orderDao.approveClient(idComboBox.getValue(), organiser.getId());
            ShowAlert.showAlert("Klient został zatwierdzony");
        });
    }

    protected void markAsFinalised(Organiser organiser) {
        localOrganiser = organiser;
        List<Order> updatedOrders = orderDao.getUpdatedOrdersOrganiser(organiser.getId());

        if (updatedOrders.isEmpty()) {
            hideUserInput();
            ShowAlert.showAlert("Brak zamowien do zatwierdzenia");
            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(updatedOrders);
        createOrderTableView(ordersObservable, true);
        text.setText("Zamówienie, którego klienta zatwierdzasz:");
        populateComboBoxOrderId(updatedOrders);

        confirmButton.setOnAction(event -> {
            orderDao.markAsFinalised(idComboBox.getValue(), organiser.getId());
            ShowAlert.showAlert("Zatwierdzono zamówienie");
        });
    }

    @FXML
    void goBack(MouseEvent event) throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/organiser/gui/organiser-main-view.fxml",
                controller -> {((OrganiserViewController) controller).setOrganiser(localOrganiser);}, user.toString());
    }
}
