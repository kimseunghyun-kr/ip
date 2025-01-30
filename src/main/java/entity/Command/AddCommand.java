package entity.Command;

import exceptions.UserFacingException;
import service.ITaskService;

import java.util.List;

/**
 * Represents the "Add / insertion" command in the task management system.
 * This command interacts with {@link ITaskService} to add a new task
 * based on the provided parameters.
 */
public class AddCommand implements Command {
    /** The task service used to manage tasks. */
    private ITaskService taskService;

    /**
     * Sets the task service for this command.
     *
     * @param taskService The task service instance to be used.
     */
    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Executes the "Add" command by adding a new task.
     *
     * @param parameters A list of parameters where the first element is the task type
     *                   and the second element is the task description.
     * @throws UserFacingException if fewer than two parameters are provided.
     */
    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() < 2) {
            throw new UserFacingException("Add command requires at least 2 parameters: type and description");
        }
        String response = taskService.addTask(parameters);
        System.out.println(response);
    }
}
