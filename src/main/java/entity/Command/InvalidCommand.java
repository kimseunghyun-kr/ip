package entity.Command;

import service.TaskService;

import java.util.List;

public class InvalidCommand implements Command {
    private TaskService taskService;
    @Override
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        System.out.println("INVALID command. No operation performed.");
    }
}
