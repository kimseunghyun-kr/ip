package entity.Command;

import exceptions.UserFacingException;
import service.ITaskService;

import java.util.List;

public class AddCommand implements Command {
    private ITaskService taskService;

    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() < 2) {
            throw new UserFacingException("Add command requires at least 2 parameters: type and description");
        }
        String response = taskService.addTask(parameters);
        System.out.println(response);
    }
}
