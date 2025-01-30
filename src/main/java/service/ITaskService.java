package service;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;
import entity.TaskType;

import java.time.LocalDateTime;
import java.util.List;

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
    String markDone(int index);

    /**
     * Marks a task as undone.
     *
     * @param index The index of the task to be marked as not completed.
     * @return A confirmation message.
     */
    @ExceptionHandler
    String markUndone(int index);

    /**
     * Retrieves all tasks.
     *
     * @return A formatted string containing all tasks.
     */
    @ExceptionHandler
    String getAllTasks();

    /**
     * Adds a new task based on provided parameters.
     *
     * @param taskParams A list of parameters describing the task.
     * @return A confirmation message.
     */
    @ExceptionHandler
    String addTask(List<String> taskParams);

    /**
     * Deletes a task by its identifier.
     *
     * @param taskId The ID of the task to delete.
     * @return A confirmation message.
     */
    @ExceptionHandler
    String deleteTask(int taskId);

    /**
     * Searches for tasks by date range and type.
     *
     * @param type The type of tasks to search for.
     * @param from The start date of the search range.
     * @param to   The end date of the search range.
     * @return A formatted string containing matching tasks.
     */
    @ExceptionHandler
    String SearchByDate(TaskType type, LocalDateTime from, LocalDateTime to);

    /**
     * Searches for a task by its unique identifier.
     *
     * @param uuid The unique identifier of the task.
     * @return A formatted string containing the matching task.
     */
    @ExceptionHandler
    String SearchOrder(String uuid);

    /**
     * Searches for tasks by a keyword.
     *
     * @param keyword The keyword to filter tasks.
     * @return A formatted string containing matching tasks.
     */
    @ExceptionHandler
    String SearchByKeyword(String keyword);
}
