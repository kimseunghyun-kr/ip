package controller;

import java.time.LocalDateTime;
import java.util.List;

import entity.TaskType;

/**
 * Defines the common interface for different task controllers (CLI, GUI).
 * Provides task management functionalities.
 */
public interface ITaskController {

    /**
     * Marks a task as completed.
     *
     * @param index The index of the task to mark as done.
     * @return A response message indicating the task's updated status.
     */
    ControllerResponse markDone(int index);

    /**
     * Marks a task as not completed.
     *
     * @param index The index of the task to mark as undone.
     * @return A response message indicating the task's updated status.
     */
    ControllerResponse markUndone(int index);

    /**
     * Retrieves all tasks in the system.
     *
     * @return A formatted string representing all tasks.
     */
    ControllerResponse getAllTasks();

    /**
     * Adds a new task to the system.
     *
     * @param taskParams A list of parameters describing the task.
     * @return A response message confirming the task addition.
     */
    ControllerResponse addTask(List<String> taskParams);

    /**
     * Deletes a task by its identifier.
     *
     * @param taskId The ID of the task to delete.
     * @return A response message indicating the deleted task.
     */
    ControllerResponse deleteTask(int taskId);

    /**
     * Searches for a task by its unique identifier.
     *
     * @param uuidStr The unique identifier of the task.
     * @return A response message containing the task's position in the list.
     */
    ControllerResponse searchOrder(String uuidStr);

    /**
     * Searches for tasks that contain a given keyword.
     *
     * @param keyword The keyword to filter tasks.
     * @return A formatted string listing matching tasks.
     */
    ControllerResponse searchByKeyword(String keyword);

    /**
     * Searches for tasks within a specified date range and type.
     *
     * @param type The type of tasks to search for.
     * @param from The start date of the search range.
     * @param to   The end date of the search range.
     * @return A formatted string listing tasks within the given date range.
     */
    ControllerResponse searchByDate(TaskType type, LocalDateTime from, LocalDateTime to);
}
