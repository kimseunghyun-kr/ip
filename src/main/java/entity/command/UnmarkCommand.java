package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import exceptions.UserFacingException;
import service.ITaskService;



/**
 * Represents the "mark " command in the task management system.
 * This command interacts with {@link ITaskService} to mark a task incomplete
 * based on the provided parameters.
 */
public class UnmarkCommand implements Command {

    private ITaskController taskController;

    @Override
    public void setTaskController(ITaskController taskController) {
        this.taskController = taskController;
    }

    @Override
    public ControllerResponse execute(List<String> parameters) {
        if (parameters.size() != 1) {
            throw new UserFacingException("unmark command requires exactly 1 parameter");
        }

        int taskId = Integer.parseInt(parameters.get(0));
        return taskController.markUndone(taskId);
    }

}
