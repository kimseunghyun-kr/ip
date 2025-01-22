package entity.Command;

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
        if (parameters.size() != 1) {
            throw new IllegalArgumentException("Add command requires exactly 1 parameter");
        }
        String task = parameters.get(0);
        String resposne = taskService.addTask(task);
        System.out.println(resposne);
    }
}
