package util.config;

import controller.ITaskController;
import gui.components.MainWindow;
import gui.components.TaskUpdateDialogController;
import service.CommandExecutionService;

public class FxmlStaticSetterInjectionConfig {
    private final CommandExecutionService commandExecutionService;
    private final ITaskController taskController;

    public FxmlStaticSetterInjectionConfig(CommandExecutionService commandExecutionService,
                                           ITaskController taskController) {
        this.commandExecutionService = commandExecutionService;
        this.taskController = taskController;
    }

    public void injectSetter() {
        MainWindow.setCommandExecutionService(commandExecutionService);
        TaskUpdateDialogController.setTaskController(taskController);
    }
}
