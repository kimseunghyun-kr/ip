package entity.Command;

import service.TaskService;

import java.util.List;

public interface Command {
    void setTaskService(TaskService taskService);
    void execute(List<String> parameters);
}
