package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import exceptions.UserFacingException;
import service.ITaskService;



/**
 * Represents the "Add / insertion" command in the task management system.
 * This command interacts with {@link ITaskService} to add a new task
 * based on the provided parameters.
 */
public class AddCommand implements Command {
    /** The task service used to manage tasks. */
    private ITaskController taskController;

    /**
     * Sets the task service for this command.
     *
     * @param taskController The task service instance to be used.
     */
    @Override
    public void setTaskController(ITaskController taskController) {
        this.taskController = taskController;
    }

    /**
     * Executes the "Add" command by adding a new task.
     *
     * @param parameters A list of parameters where the first element is the task type
     *                   and the second element is the task description.
     * @throws UserFacingException if fewer than two parameters are provided.
     */
    @Override
    public ControllerResponse execute(List<String> parameters) {
        if (parameters.size() < 2) {
            throw new UserFacingException("Add command requires at least 2 parameters: type and description");
        }
        return taskController.addTask(parameters);
    }
}
