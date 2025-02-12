import static util.config.DiConfig.registerConfig;

import java.nio.file.Path;

import dicontainer.DependencyInjectionContainer;
import dispatcher.GuiDispatcher;
import runtime.IBotRunTime;
import util.DirectoryInitializeUtils;
import util.config.FxmlStaticSetterInjectionConfig;


/**
 * The {@code Spring} class serves as the entry point for launching either the GUI or CLI runtime.
 * It initializes the Dependency Injection (DI) container, sets up required configurations,
 * and starts the appropriate runtime environment.
 */
public class Spring {

    /**
     * The main method to start the application.
     *
     * @param args command-line arguments passed during application startup.
     */
    public static void main(String[] args) {
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        launchGuiMode(container, args);
    }

    /**
     * Launches the GUI runtime by initializing required dependencies and setting up the environment.
     *
     * @param container The Dependency Injection (DI) container.
     * @param args      Command-line arguments passed for GUI configuration.
     */
    private static void launchGuiMode(DependencyInjectionContainer container, String[] args) {
        // Initialize the directories
        Path dataDirectoryPath = DirectoryInitializeUtils.initializeDataDirectory();
        Path logDirectoryPath = DirectoryInitializeUtils.initializeLogDirectory();

        // Register configurations in the DI container
        registerConfig(container, logDirectoryPath, dataDirectoryPath, false);
        // Pre-initialize all dependencies
        container.initialize();
        // Start the GUI runtime
        IBotRunTime botRuntime = container.resolve(IBotRunTime.class);
        FxmlStaticSetterInjectionConfig fxmlConfig = container.resolve(FxmlStaticSetterInjectionConfig.class);
        fxmlConfig.injectSetter();
        botRuntime.run();
    }

    /**
     * Launches the CLI runtime by initializing required dependencies and setting up the environment.
     *
     * @param container The Dependency Injection (DI) container.
     */
    private static void launchCliMode(DependencyInjectionContainer container) {
        // Initialize the directories
        Path dataDirectoryPath = DirectoryInitializeUtils.initializeDataDirectory();
        Path logDirectoryPath = DirectoryInitializeUtils.initializeLogDirectory();

        // Register configurations in the DI container
        registerConfig(container, logDirectoryPath, dataDirectoryPath, true);

        // Pre-initialize all dependencies
        container.initialize();

        // Start the CLI runtime
        IBotRunTime botRuntime = container.resolve(IBotRunTime.class);
        botRuntime.run();
    }
}


