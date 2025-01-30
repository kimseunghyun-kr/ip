package entity.Command;

import exceptions.UserFacingException;
import service.ITaskService;

import java.util.List;

public class TerminationCommand implements Command {
    private ITaskService taskService;

    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (!parameters.isEmpty()) {
            throw new UserFacingException("is this a bye? or just a chat?");
        }
    }
}
