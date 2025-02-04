import static util.DiConfig.registerConfig;

import java.nio.file.Path;

import dicontainer.DependencyInjectionContainer;
import dispatcher.GuiDispatcher;
import runtime.IBotRunTime;
import util.DirectoryInitializeUtils;


public class Spring {
    public static void main(String[] args) {
        DependencyInjectionContainer container = new DependencyInjectionContainer();
        guiLaunch(container, args);
    }

    private static void guiLaunch(DependencyInjectionContainer container, String[] args) {
        // Initialize the DI container
        Path filePath = DirectoryInitializeUtils.initializeDataDirectory();
        Path logPath = DirectoryInitializeUtils.initializeLogDirectory();
        registerConfig(container, logPath, filePath, false);
        // Pre-initialize all dependencies
        container.initialize();

        // Start the CLI runtime
        IBotRunTime botRunTime = container.resolve(IBotRunTime.class);
        GuiDispatcher guiDispatcher = container.resolve(GuiDispatcher.class);
        guiDispatcher.setArgs(args);
        botRunTime.run();
    }

    private static void cliLaunch(DependencyInjectionContainer container) {
        // Initialize the DI container
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
