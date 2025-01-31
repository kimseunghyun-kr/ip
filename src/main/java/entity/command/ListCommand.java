package entity.command;

import java.util.List;

import exceptions.UserFacingException;
import service.ITaskService;



/**
 * Represents the "Listing " command in the task management system.
 * This command interacts with {@link ITaskService} to list all tasks
 * based on the provided parameters.
 */
public class ListCommand implements Command {
    private ITaskService taskService;

    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (!parameters.isEmpty()) {
            throw new UserFacingException("list command requires no parameter");
        }
        String task = taskService.getAllTasks();
        System.out.println(task);
    }
}
