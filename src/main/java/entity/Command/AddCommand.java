package entity.Command;

import entity.tasks.Task;
import entity.tasks.TaskFactory;
import service.TaskService;

import java.util.List;

public class AddCommand implements Command {
    private TaskService taskService;

    @Override
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() < 2) {
            throw new IllegalArgumentException("Add command requires at least 2 parameters: type and description");
        }
        String response = taskService.addTask(parameters);
        System.out.println(response);
    }
}
