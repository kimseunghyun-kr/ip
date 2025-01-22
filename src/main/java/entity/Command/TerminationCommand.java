package entity.Command;

import service.TaskService;

import java.util.List;

public class TerminationCommand implements Command {
    private TaskService taskService;

    @Override
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("is this a bye? or just a chat?");
        }
    }
}
