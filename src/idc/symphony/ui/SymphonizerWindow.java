package idc.symphony.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Application entry point - binding to FXML and therefore controller;
 */
public class SymphonizerWindow extends Application {
    public Stage windowStage;
    public SymphonizerController controller;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        windowStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/SymphonizerWindow.fxml"));
        Parent root = loader.load();

        // Inject application into controller
        controller = loader.getController();
        controller.application = this;

        primaryStage.getIcons().add(new Image(getClass().getResource("resources/icon.png").toExternalForm()));
        primaryStage.setTitle("IDC Symphonizer");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(540);
        primaryStage.setMinHeight(370);
        primaryStage.initStyle(StageStyle.UNIFIED);

        controller.loadState();
    }

    public void resize(int width, int height) {
        windowStage.setWidth(width);
        windowStage.setHeight(height);
    }

    public void reposition(int x, int y) {
        windowStage.setX(x);
        windowStage.setY(y);
    }

    public void maximized(boolean maximized) {
        windowStage.setMaximized(maximized);
    }

    public void show() {
        windowStage.show();
    }

    @Override
    public void stop() {
        controller.shutdown();
    }
}
