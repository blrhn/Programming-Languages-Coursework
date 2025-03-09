package controlcenter.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("control-center-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Centrala");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void main(String[] args) {
        launch();
    }
}