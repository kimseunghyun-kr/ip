package entity.Command;

import service.TaskService;

import java.util.List;

public class UnmarkCommand implements Command {

    private TaskService taskService;

    @Override
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() != 1) {
            throw new IllegalArgumentException("unmark command requires exactly 1 parameter");
        }

        int taskId = Integer.parseInt(parameters.getFirst());
        String response = taskService.markUndone(taskId);
        System.out.println(response);
    }

}
