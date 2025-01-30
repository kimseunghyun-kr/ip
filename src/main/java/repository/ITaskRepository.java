package repository;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;
import entity.TaskType;
import entity.tasks.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ProxyEnabled(implementation = FileBackedTaskRepository.class)
public interface ITaskRepository extends CrudRepository<Task, Integer>, Proxiable {

    // We can override methods from CrudRepository to add custom behavior,
    // or to annotate them with @ExceptionHandler for AOP.

    @Override
    @ExceptionHandler
    Task save(Task entity);

    @Override
    @ExceptionHandler(returnsDefault = Optional.class)
    Optional<Task> findByOrder(Integer id);

    @Override
    @ExceptionHandler
    List<Task> findAll();

    @Override
    @ExceptionHandler
    Task deleteById(Integer id);

    Integer remainingTasks();

    List<Task> findAllFromWhenToWhen(TaskType type, LocalDateTime from, LocalDateTime to);

    int findOrder(UUID uuid);

    List<Task> findTaskWithKeyword(String keyword);
}
