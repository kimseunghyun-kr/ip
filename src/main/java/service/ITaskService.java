package service;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;

import java.util.List;

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
}
