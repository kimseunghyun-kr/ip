package gui;

import static util.DiConfig.registerConfig;

import java.io.IOException;
import java.nio.file.Path;

import dicontainer.DependencyInjectionContainer;
import gui.components.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.entityManager.TaskFlusher;
import runtime.ActionHandler;
import util.DirectoryInitializeUtils;


/**
 * launcher class for the GUI javafx application
 */
public class JavaFxLauncher extends Application {

    //    private final Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/nezuko.jpg"));
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
            stage.setTitle("REM Task Manager"); // Set the title of the application
            stage.setResizable(true);


            // Initialize the DI container
            DependencyInjectionContainer container = new DependencyInjectionContainer();
            Path filePath = DirectoryInitializeUtils.initializeDataDirectory();
            Path logPath = DirectoryInitializeUtils.initializeLogDirectory();

            registerConfig(container, logPath, filePath, false);
            // Pre-initialize all dependencies
            container.initialize();
            ActionHandler actionhandler = container.resolve(ActionHandler.class);

            //initiate taskFlusher
            TaskFlusher taskFlusher = container.resolve(TaskFlusher.class);
            taskFlusher.start();
            fxmlLoader.<MainWindow>getController().setActionHandler(actionhandler);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
