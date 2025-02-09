package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;


/**
 * Represents the "Invalid / No-op" command in the task management system.
 * Behaves more closely as a sentinel value to determine when to trigger exception for invalid commands
 */
public class InvalidCommand implements Command {
    // Predefined sentinel responses
    public static final ControllerResponse<String> INVALID_COMMAND =
            new ControllerResponse<>("INVALID command. No operation performed.", "ERROR");

    @Override
    public void setTaskController(ITaskController taskController) {
    }

    @Override
    public ControllerResponse<String> execute(List<String> parameters) {
        return INVALID_COMMAND;
    }
}
