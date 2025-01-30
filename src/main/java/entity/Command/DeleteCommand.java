package entity.Command;

import exceptions.UserFacingException;
import service.ITaskService;

import java.util.List;

public class DeleteCommand implements Command {
    private ITaskService taskService;
    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            throw new UserFacingException("Delete command requires one parameter - size");
        }
        int taskId = Integer.parseInt(parameters.get(0));
        String task = taskService.deleteTask(taskId);
        System.out.println(task);
    }
}
