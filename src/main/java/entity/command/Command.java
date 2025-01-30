package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import service.ITaskService;

/**
 * Represents a command that can be executed in the task management system.
 * Commands interact with {@link ITaskService} to perform actions.
 */
public interface Command {
    /**
     * Sets the task service for the command.
     *
     * @param taskService The task service instance to be used.
     */
    void setTaskController(ITaskController taskService);

    /**
     * Executes the command with the given parameters.
     *
     * @param parameters A list of parameters required for the command execution.
     */
    ControllerResponse execute(List<String> parameters);
}
