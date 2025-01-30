package service;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;
import entity.TaskType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ProxyEnabled(implementation = TaskService.class)
public interface ITaskService extends Proxiable {
    @ExceptionHandler
    String markDone(int index);
    @ExceptionHandler
    String markUndone(int index);
    @ExceptionHandler
    String getAllTasks();
    @ExceptionHandler
    String addTask(List<String> taskParams);
    @ExceptionHandler
    String deleteTask(int taskId);
    @ExceptionHandler
    public String SearchByDate(TaskType type, LocalDateTime from, LocalDateTime to);
    @ExceptionHandler
    String SearchOrder(String uuid);

}
