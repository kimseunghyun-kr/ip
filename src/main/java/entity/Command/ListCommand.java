package entity.Command;

import service.TaskService;

import java.util.List;

public class ListCommand implements Command {
    private TaskService taskService;

    @Override
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("list command requires no parameter");
        }
        String task = taskService.getAllTasks();
        System.out.println(task);
    }
}
