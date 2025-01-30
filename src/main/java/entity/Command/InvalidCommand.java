package entity.Command;

import service.ITaskService;

import java.util.List;

public class InvalidCommand implements Command {
    private ITaskService taskService;
    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        System.out.println("INVALID command. No operation performed.");
    }
}
