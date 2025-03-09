package app.gui.shared.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SceneSwitcher {
    public static <U> void switchScene(Stage stage, String fxml, Consumer<U> controller, String text) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxml));
        Parent root = loader.load();
        controller.accept(loader.getController());
        stage.setTitle(text);
        stage.setScene(new Scene(root));
    }

    public static <U, T> void biSwitchScene(Stage stage, String fxml, BiConsumer<U, T> controller, T userTemp, String text) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxml));
        Parent root = loader.load();
        controller.accept(loader.getController(), userTemp);
        stage.setTitle(text);
        stage.setScene(new Scene(root));
    }
}
