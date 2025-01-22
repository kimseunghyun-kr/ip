package repository;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;
import entity.tasks.Task;

@ProxyEnabled(implementation = TaskRepository.class)
public interface ITaskRepository extends Proxiable {
    String store(Task input);
    String getAll();
    @ExceptionHandler
    Task getById(int index);
}

