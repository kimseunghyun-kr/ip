package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import exceptions.UserFacingException;
import service.ITaskService;



/**
 * Represents the "Listing " command in the task management system.
 * This command interacts with {@link ITaskService} to list all tasks
 * based on the provided parameters.
 */
public class ListCommand implements Command {
    private ITaskController taskController;

    @Override
    public void setTaskController(ITaskController taskController) {
        this.taskController = taskController;
    }

    @Override
    public ControllerResponse execute(List<String> parameters) {
        if (!parameters.isEmpty()) {
            throw new UserFacingException("list command requires no parameter");
        }
        return taskController.getAllTasks();
    }
}
