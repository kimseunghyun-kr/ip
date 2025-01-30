package entity.Command;

import service.ITaskService;

import java.util.List;

public interface Command {
    void setTaskService(ITaskService taskService);
    void execute(List<String> parameters);
}
