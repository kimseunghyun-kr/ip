package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import exceptions.UserFacingException;



/**
 * Represents the "Termination / No-op" command in the task management system.
 * Behaves more closely as a sentinel value to determine when to stop runtime
 */
public class TerminationCommand implements Command {

    public static final ControllerResponse SIGTERM_INPUT =
            new ControllerResponse("bye bye hope to see you next time");

    @Override
    public void setTaskController(ITaskController taskController) {
    }

    @Override
    public ControllerResponse execute(List<String> parameters) {
        if (!parameters.isEmpty()) {
            throw new UserFacingException("is this a bye? or just a chat?");
        }
        return SIGTERM_INPUT;
    }
}
