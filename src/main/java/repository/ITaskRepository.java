package repository;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;
import entity.tasks.Task;

import java.util.List;
import java.util.Optional;

@ProxyEnabled(implementation = FileBackedTaskRepository.class)
public interface ITaskRepository extends CrudRepository<Task, Integer>, Proxiable {

    // We can override methods from CrudRepository to add custom behavior,
    // or to annotate them with @ExceptionHandler for AOP.

    @Override
    @ExceptionHandler
    Task save(Task entity);

    @Override
    @ExceptionHandler(returnsDefault = Optional.class)
    Optional<Task> findById(Integer id);

    @Override
    @ExceptionHandler
    List<Task> findAll();

    @Override
    @ExceptionHandler
    Task deleteById(Integer id);

    Integer remainingTasks();
}
