package service;

import java.time.LocalDateTime;
import java.util.List;

import dicontainer.Proxiable;
import dicontainer.aopinterfaces.annotationinterfaces.ExceptionHandler;
import dicontainer.aopinterfaces.annotationinterfaces.ProxyEnabled;
import entity.TaskType;
import entity.tasks.Task;


/**
 * Defines the contract for task-related operations in the system.
 * This interface is proxy-enabled and supports exception handling via AOP.
 * The concrete implementation is {@link TaskService}.
 */
@ProxyEnabled(implementation = TaskService.class)
public interface ITaskService extends Proxiable {

    /**
     * Marks a task as done.
     *
     * @param index The index of the task to be marked as completed.
     * @return A confirmation message.
     */
    @ExceptionHandler
    Task markDone(int index);

    /**
     * Marks a task as undone.
     *
     * @param index The index of the task to be marked as not completed.
     * @return A confirmation message.
     */
    @ExceptionHandler
    Task markUndone(int index);

    /**
     * Retrieves all tasks.
     *
     * @return A formatted string containing all tasks.
     */
    @ExceptionHandler
    List<Task> getAllTasks();

    /**
     * Adds a new task based on provided parameters.
     *
     * @param taskParams A list of parameters describing the task.
     * @return A confirmation message.
     */
    @ExceptionHandler
    Task addTask(List<String> taskParams);

    /**
     * Deletes a task by its identifier.
     *
     * @param taskId The ID of the task to delete.
     * @return A confirmation message.
     */
    @ExceptionHandler
    Task deleteTask(int taskId);

    /**
     * Searches for tasks by date range and type.
     *
     * @param type The type of tasks to search for.
     * @param from The start date of the search range.
     * @param to   The end date of the search range.
     * @return A formatted string containing matching tasks.
     */
    @ExceptionHandler
    List<Task> searchByDate(TaskType type, LocalDateTime from, LocalDateTime to);

    /**
     * Searches for a task by its unique identifier.
     *
     * @param uuid The unique identifier of the task.
     * @return A formatted string containing the matching task.
     */
    @ExceptionHandler
    int searchOrder(String uuid);

    /**
     * Searches for tasks by a keyword.
     *
     * @param keyword The keyword to filter tasks.
     * @return A formatted string containing matching tasks.
     */
    @ExceptionHandler
    List<Task> searchByKeyword(String keyword);

    /**
     * Searches for tasks by a keyword.
     *
     * @return A List of all Tasks Deleted.
     */
    @ExceptionHandler
    List<Task> deleteAll();
}
