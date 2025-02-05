package util.config;

import controller.ITaskController;
import gui.components.MainWindow;
import gui.components.TaskUpdateDialogController;
import service.CommandExecutionService;

/**
 * Configures static setter-based dependency injection for FXML controllers.
 * <p>
 * This class is responsible for injecting required dependencies into
 * JavaFX controllers that rely on static setters for initialization.
 * </p>
 */
public class FxmlStaticSetterInjectionConfig {
    private final CommandExecutionService commandExecutionService;
    private final ITaskController taskController;

    /**
     * Constructs an instance of {@code FxmlStaticSetterInjectionConfig} with the required dependencies.
     *
     * @param commandExecutionService The service responsible for executing user commands.
     * @param taskController          The task controller managing task operations.
     */
    public FxmlStaticSetterInjectionConfig(CommandExecutionService commandExecutionService,
                                           ITaskController taskController) {
        this.commandExecutionService = commandExecutionService;
        this.taskController = taskController;
    }

    /**
     * Injects dependencies into FXML controllers via static setter methods.
     * <p>
     * This method ensures that controllers such as {@code MainWindow} and
     * {@code TaskUpdateDialogController} receive the necessary dependencies.
     * </p>
     */
    public void injectSetter() {
        MainWindow.setCommandExecutionService(commandExecutionService);
        TaskUpdateDialogController.setTaskController(taskController);
    }
}
