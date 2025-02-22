package gui;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * launcher class for the GUI javafx application
 */
public class JavaFxLauncher extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(JavaFxLauncher.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);

            //css
            String css = Objects.requireNonNull(this.getClass().getResource("/css/Stage.css")).toExternalForm();
            scene.getStylesheets().add(css);
            // Window formatting
            stage.setTitle("Spring - Task Manager"); // Set the title of the application
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
