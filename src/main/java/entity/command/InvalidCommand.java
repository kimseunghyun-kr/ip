package entity.command;

import java.util.List;

import service.ITaskService;



/**
 * Represents the "Invalid / No-op" command in the task management system.
 * Behaves more closely as a sentinel value to determine when to trigger exception for invalid commands
 */
public class InvalidCommand implements Command {
    @Override
    public void setTaskService(ITaskService taskService) {
    }

    @Override
    public void execute(List<String> parameters) {
        System.out.println("INVALID command. No operation performed.");
    }
}
