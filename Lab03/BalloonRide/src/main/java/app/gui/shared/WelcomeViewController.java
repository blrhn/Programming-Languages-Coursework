package app.gui.shared;

import app.gui.shared.utils.enums.Mode;
import app.gui.shared.utils.SceneSwitcher;
import app.gui.shared.utils.enums.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeViewController {

    @FXML private Button loginButton;
    @FXML private Text welcomeText;

    private User user;

    public void setUserTypeSettings(User user) {
        this.user = user;
        welcomeText.setText(user.getText());
    }

    @FXML
    private void goLogin(MouseEvent event) throws IOException {
        setScene(Mode.LOGIN, "LOG IN");
    }

    @FXML
    private void goRegister(MouseEvent event) throws IOException {
        setScene(Mode.REGISTRATION, "REGISTRATION");
    }

    private void setScene(Mode mode, String title) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/shared/login-view.fxml", controller -> {((LoginViewController) controller).setSettings(user, mode);}, title);
    }


}
