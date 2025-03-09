package app.gui.shared;

import app.gui.client.controller.ClientViewController;
import app.gui.organiser.controller.OrganiserViewController;
import app.gui.seller.controller.SellerViewController;
import app.gui.shared.utils.ShowAlert;
import app.gui.shared.utils.enums.Mode;
import app.gui.shared.utils.SceneSwitcher;
import app.gui.shared.utils.enums.User;
import service.dao.UserDao;
import service.impl.ClientDaoImpl;
import service.impl.SellerDaoImpl;
import service.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.exceptions.ObjectNullException;
import service.impl.OrganiserDaoImpl;

import java.io.IOException;
import java.util.function.BiConsumer;

public class LoginViewController {

    @FXML private Button actionButton;
    @FXML private TextField name;
    @FXML private Button goBackButton;

    private Mode mode;
    private User user;

    private void setMode(Mode mode) {
        this.mode = mode;

        if (mode == Mode.LOGIN) {
            actionButton.setText("Zaloguj się");
        } else if (mode == Mode.REGISTRATION) {
            actionButton.setText("Zarejestruj się");
        }
    }

    public void setSettings(User user, Mode mode) {
        this.user = user;
        setMode(mode);
    }

    @FXML
    private void handleClick(MouseEvent event) throws ObjectNullException, IOException {
        switch(user) {
            case User.CLIENT -> handleClient();
            case User.ORGANISER -> handleOrganiser();
            case User.SELLER -> handleSeller();
            default -> throw new ObjectNullException();
        }

    }

    @FXML
    void goBack(MouseEvent event) throws IOException {
        Stage stage = (Stage) goBackButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/shared/welcome-view.fxml", controller -> {((WelcomeViewController) controller).setUserTypeSettings(user);}, user.toString());
    }

    private <T, U> void handleUser(UserDao<T> userDao, UserFactory<T> userFactory, String fxml, BiConsumer<U, T> controller)
            throws IOException {
        Authenticate<T> auth = new Authenticate<>(userDao, userFactory);

        try {
            T tempUser = (mode == Mode.LOGIN) ? auth.login(name.getText()) : auth.register(name.getText());

            Stage stage = (Stage) actionButton.getScene().getWindow();
            SceneSwitcher.biSwitchScene(stage, fxml, controller, tempUser, user.toString());


        } catch (ObjectNullException e) {
            ShowAlert.showAlert("Nie udało się zalogować");

        }
    }

    private void handleClient() throws IOException {
        handleUser(new ClientDaoImpl(), new ClientFactory(), "/client/gui/client-main-view.fxml",
                (controller, user) -> {((ClientViewController) controller).setClient(user);});

    }

    private void handleOrganiser() throws IOException {
        handleUser(new OrganiserDaoImpl(), new OrganiserFactory(), "/organiser/gui/organiser-main-view.fxml",
                (controller, user) -> {((OrganiserViewController) controller).setOrganiser(user);});
    }

    private void handleSeller() throws IOException {
        handleUser(new SellerDaoImpl(), new SellerFactory(), "/seller/gui/seller-main-view.fxml",
                (controller, user) -> {((SellerViewController) controller).setSeller(user);});
    }
}
