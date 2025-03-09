package tailor.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("tailor-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Środowisko");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void main(String[] args) {
        launch();
    }
}
