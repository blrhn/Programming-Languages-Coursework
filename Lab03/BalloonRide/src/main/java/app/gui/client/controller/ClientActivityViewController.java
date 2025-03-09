package app.gui.client.controller;

import app.gui.shared.utils.SceneSwitcher;
import app.gui.shared.utils.ShowAlert;
import app.gui.shared.utils.enums.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.dao.OfferDao;
import service.dao.OrderDao;
import service.impl.OfferDaoImpl;
import service.impl.OrderDaoImpl;
import service.model.Client;
import service.model.Offer;
import service.model.Order;

import java.io.IOException;
import java.util.List;


public class ClientActivityViewController {
    @FXML private GridPane gridPane;
    @FXML private Button confirmButton;
    @FXML private ComboBox<Integer> idComboBox;
    @FXML private Text text;

    private final TableView<Offer> offerTable = new TableView<>();
    private final TableView<Order> orderTable = new TableView<>();

    private static final OfferDao offerDao = new OfferDaoImpl();
    private static final OrderDao orderDao = new OrderDaoImpl();

    private Client localClient;
    private final User user = User.CLIENT;

    public void initialize() {
        setUserInput(false, false, false);
    }

    private void setUserInput(boolean showButton, boolean showComboBox, boolean showText) {
        confirmButton.setVisible(showButton);
        idComboBox.setVisible(showComboBox);
        text.setVisible(showText);
    }

    private void createOfferTableView(ObservableList<Offer> offerList) {
        offerTable.getItems().clear();

        TableColumn<Offer, Integer> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Offer, String> name = new TableColumn<>("Nazwa");
        name.setCellValueFactory(new PropertyValueFactory<>("title"));

        offerTable.getColumns().add(id);
        offerTable.getColumns().add(name);
        offerTable.setItems(offerList);

        gridPane.add(offerTable, 0, 0);
    }

    private void createOrderTableView(ObservableList<Order> orderList, boolean showDeclarationStatus, boolean showOrderStatus) {
        orderTable.getItems().clear();

        TableColumn<Order, Integer> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Order, String> name = new TableColumn<>("Nazwa");
        name.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOffer().getTitle()));
        TableColumn<Order, String> date = new TableColumn<>("Data realizacji");
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        orderTable.getColumns().add(id);
        orderTable.getColumns().add(name);
        orderTable.getColumns().add(date);

        if (showOrderStatus) {
            TableColumn<Order, String> status = new TableColumn<>("Status zamówienia");
            status.setCellValueFactory(new PropertyValueFactory<>("status"));
            orderTable.getColumns().add(status);
        } else if (showDeclarationStatus) {
            TableColumn<Order, String> declarationStatus = new TableColumn<>("Wynik zgłoszenia");
            declarationStatus.setCellValueFactory(cellData -> {
                String result = (cellData.getValue().getClientApproved() == 0)
                        ? "Niestety nie zostałeś wybrany"
                        : "Gratulacje, zapraszamy na lot!";
                return new SimpleStringProperty(result);
            });
            orderTable.getColumns().add(declarationStatus);
        }

        orderTable.setItems(orderList);

        gridPane.add(orderTable, 0, 0);
    }

    private void populateComboBoxOfferId(List<Offer> offers) {
        List<Integer> ids = offers.stream().map(Offer::getId).toList();

        idComboBox.getItems().addAll(ids);
    }

    private void populateComboBoxOrderId(List<Order> offers) {
        List<Integer> ids = offers.stream().map(Order::getId).toList();

        idComboBox.getItems().addAll(ids);
    }

    protected void browseAndOrder(Client client) {
        localClient = client;

        List<Offer> offers = offerDao.getOffers();

        if (offers.isEmpty()) {
            ShowAlert.showAlert("Brak dostępnych ofert");
            return;
        }

        setUserInput(true, true, true);
        ObservableList<Offer> offersObservable = FXCollections.observableArrayList(offers);
        createOfferTableView(offersObservable);
        text.setText("Oferta, w ktorej chcesz wziąć udział:");
        populateComboBoxOfferId(offers);

        confirmButton.setOnAction(event -> {
            Offer offer = offerDao.getOffer(idComboBox.getValue());

            if (offer == null) {
                ShowAlert.showAlert("Oferta nie istnieje");
                return;
            }

            Order order = new Order(client.getId(), offer);
            orderDao.addOrder(order);
            ShowAlert.showAlert("Zapisano Twój wybór");
        });
    }

    protected void printOrders(Client client) {
        localClient = client;
        List<Order> orders = orderDao.getClientOrders(client.getId());

        if (orders.isEmpty()) {
            ShowAlert.showAlert("Brak zamówień");
            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(orders);
        createOrderTableView(ordersObservable, false, true);
    }

    protected void declareParticipation(Client client) {
        localClient = client;
        List<Order> ordersWithDates = orderDao.getOrdersWithDates(client.getId());

        if (ordersWithDates.isEmpty()) {
            ShowAlert.showAlert("Brak zamówień");
            return;
        }

        setUserInput(true, true, true);
        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(ordersWithDates);
        createOrderTableView(ordersObservable, false, true);
        text.setText("Zamówienie, w ktorym chcesz wziąć udział:");
        populateComboBoxOrderId(ordersWithDates);

        confirmButton.setOnAction(event -> {
            orderDao.setClientDeclaration(idComboBox.getValue());
            ShowAlert.showAlert("Zgłoszono chęć uczestnictwa");
        });
    }

    protected void checkDeclarationStatus(Client client) {
        localClient = client;
        List<Order> updatedOrders = orderDao.getUpdatedOrders(client.getId());

        if (updatedOrders.isEmpty()) {
            ShowAlert.showAlert("Brak zamowien z potwierdzonym uczestnictwem");
            return;
        }

        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(updatedOrders);
        createOrderTableView(ordersObservable, true, false);
    }

    protected void deleteCompletedOrders(Client client) {
        localClient = client;
        List<Order> completedOrders = orderDao.getCompletedOrders(client.getId());

        if (completedOrders.isEmpty()) {
            ShowAlert.showAlert("Brak zamowien do usuniecia");
            return;
        }

        setUserInput(true, true, true);
        ObservableList<Order> ordersObservable = FXCollections.observableArrayList(completedOrders);
        createOrderTableView(ordersObservable, false, true);
        text.setText("Zamówienie, które chcesz usunąć:");
        populateComboBoxOrderId(completedOrders);

        confirmButton.setOnAction(event -> {
            orderDao.deleteCompletedOrder(idComboBox.getValue());
            ShowAlert.showAlert("Usunięto zamówienie");
        });
    }

    @FXML
    void goBack(MouseEvent event) throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/client/gui/client-main-view.fxml",
                controller -> ((ClientViewController) controller).setClient(localClient), user.toString());
    }
}
