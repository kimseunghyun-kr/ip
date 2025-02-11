package repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dicontainer.Proxiable;
import dicontainer.aopinterfaces.annotationinterfaces.ExceptionHandler;
import dicontainer.aopinterfaces.annotationinterfaces.ProxyEnabled;
import entity.TaskType;
import entity.tasks.Task;

/**
 * Repository interface for managing {@link Task} entities.
 * exists mostly only to be picked up as a JDK proxy
 * Extends {@link CrudRepository} and integrates with AOP-based exception handling.
 * Uses {@code ProxyEnabled} for dependency injection with {@code FileBackedTaskRepository}.
 */
@ProxyEnabled(implementation = FileBackedTaskRepository.class)
public interface ITaskRepository extends CrudRepository<Task, UUID>, Proxiable {

    /**
     * Saves a task entity. If the task already exists, it may be updated.
     *
     * @param entity The task entity to save.
     * @return The saved task.
     */
    @Override
    @ExceptionHandler
    Task save(Task entity);

    /**
     * Retrieves all tasks stored in the repository.
     *
     * @return A list of all tasks.
     */
    @Override
    @ExceptionHandler
    List<Task> findAll();

    /**
     * Deletes a task by its sequential order (instead of UUID).
     *
     * @param id The sequential order identifier of the task.
     * @return The deleted task.
     */
    @ExceptionHandler
    Task deleteByOrder(Integer id);

    /**
     * Finds a task by its sequential order.
     *
     * @param id The sequential order identifier of the task.
     * @return An {@link Optional} containing the found task, or empty if not found.
     */
    @ExceptionHandler(returnsDefault = Optional.class)
    Optional<Task> findByOrder(Integer id);

    /**
     * Retrieves the count of remaining tasks in the repository.
     *
     * @return The number of remaining tasks.
     */
    Integer remainingTasks();

    /**
     * Retrieves all tasks of a specific type within a given time range.
     *
     * @param type The type of tasks to filter.
     * @param from The start of the time range.
     * @param to   The end of the time range.
     * @return A list of tasks that match the specified criteria.
     */
    List<Task> findAllFromWhenToWhen(TaskType type, LocalDateTime from, LocalDateTime to);

    /**
     * Finds the sequential order of a task based on its UUID.
     *
     * @param uuid The unique identifier of the task.
     * @return The sequential order of the task.
     */
    int findOrder(UUID uuid);

    /**
     * Searches for tasks containing a specific keyword.
     *
     * @param keyword The keyword to search for in task descriptions or metadata.
     * @return A list of tasks that contain the specified keyword.
     */
    List<Task> findTaskWithKeyword(String keyword);

    List<Task> deleteAll();
}
