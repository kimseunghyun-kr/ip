package entity.Command;

import exceptions.UserFacingException;
import service.ITaskService;

import java.util.List;

public class MarkCommand implements Command {
    private ITaskService taskService;

    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() != 1) {
            throw new UserFacingException("mark command requires exactly 1 parameter");
        }

        int taskId = Integer.parseInt(parameters.get(0));
        String response = taskService.markDone(taskId);
        System.out.println(response);
    }
}
