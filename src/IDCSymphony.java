import javafx.application.Application;
import javafx.stage.Stage;
import idc.symphony.visual.Visualizer;

public class IDCSymphony extends Application {
    public static void main(String [] args) {

        launch(args);

    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("IDC Symphony");

        // Start the visualizer
        Visualizer vis = new Visualizer();
        vis.start(primaryStage);
    }
}