package entity.command;

import java.util.List;

import exceptions.UserFacingException;
import service.ITaskService;



/**
 * Represents the "Termination / No-op" command in the task management system.
 * Behaves more closely as a sentinel value to determine when to stop runtime
 */
public class TerminationCommand implements Command {

    @Override
    public void setTaskService(ITaskService taskService) {
    }

    @Override
    public void execute(List<String> parameters) {
        if (!parameters.isEmpty()) {
            throw new UserFacingException("is this a bye? or just a chat?");
        }
    }
}
