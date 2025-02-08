package gui;

import java.io.IOException;

import gui.components.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.ActionHandler;


/**
 * launcher class for the GUI javafx application
 */
public class JavaFxLauncher extends Application {
    private static ActionHandler staticActionHandler;

    // For convenience, expose static setters:
    public static void setActionHandler(ActionHandler handler) {
        staticActionHandler = handler;
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(JavaFxLauncher.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);

            //css
            String css = this.getClass().getResource("/css/Stage.css").toExternalForm();
            scene.getStylesheets().add(css);
            // Window formatting
            stage.setTitle("Task Manager"); // Set the title of the application
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setActionHandler(staticActionHandler);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
