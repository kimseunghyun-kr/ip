import static util.DiConfig.registerConfig;

import java.nio.file.Path;

import dicontainer.DependencyInjectionContainer;
import gui.JavaFxLauncher;
import javafx.application.Application;
import runtime.IBotRunTime;
import util.DirectoryInitializeUtils;



public class Spring {
    public static void main(String[] args) {
        Application.launch(JavaFxLauncher.class, args);
    }

    private static void cliLaunch() {
        // Initialize the DI container
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        Path filePath = DirectoryInitializeUtils.initializeDataDirectory();
        Path logPath = DirectoryInitializeUtils.initializeLogDirectory();

        registerConfig(container, logPath, filePath, true);

        // Pre-initialize all dependencies
        container.initialize();

        // Start the CLI runtime
        IBotRunTime botRunTime = container.resolve(IBotRunTime.class);
        botRunTime.run();
    }
}
