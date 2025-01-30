package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import exceptions.UserFacingException;
import service.ITaskService;


/**
 * Represents the "Delete / removal" command in the task management system.
 * This command interacts with {@link ITaskService} to add a new task
 * based on the provided parameters.
 */
public class DeleteCommand implements Command {
    private ITaskController taskController;
    @Override
    public void setTaskController(ITaskController taskController) {
        this.taskController = taskController;
    }

    @Override
    public ControllerResponse execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            throw new UserFacingException("Delete command requires one parameter - size");
        }
        int taskId = Integer.parseInt(parameters.get(0));
        return taskController.deleteTask(taskId);
    }
}
