package app.gui.client;

import app.gui.shared.WelcomeViewController;
import app.gui.shared.utils.enums.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientMainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/shared/welcome-view.fxml"));
        Parent root = loader.load();

        WelcomeViewController controller = loader.getController();
        controller.setUserTypeSettings(User.CLIENT);

        Scene scene = new Scene(root);
        stage.setTitle(User.CLIENT.toString());
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    public static void main(String[] args) {
        launch();
    }
}
