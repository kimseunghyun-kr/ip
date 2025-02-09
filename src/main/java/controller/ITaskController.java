package controller;

import java.time.LocalDateTime;
import java.util.List;

import entity.TaskType;
import entity.tasks.Task;
import service.dao.TaskUpdateDao;

/**
 * TaskController formats responses for user interaction.
 * Handles communication between user input (CLI/GUI) and TaskService.
 */
public interface ITaskController {

    /**
     * Marks a task as completed.
     *
     * @param index The index of the task to mark as done.
     * @return A response message indicating the task's updated status.
     */
    ControllerResponse<Task> markDone(int index);

    /**
     * Marks a task as not completed.
     *
     * @param index The index of the task to mark as undone.
     * @return A response message indicating the task's updated status.
     */
    ControllerResponse<Task> markUndone(int index);

    /**
     * Retrieves all tasks in the system.
     *
     * @return A formatted string representing all tasks.
     */
    ControllerResponse<String> getAllTasks();

    /**
     * Adds a new task to the system.
     *
     * @param taskParams A list of parameters describing the task.
     * @return A response message confirming the task addition.
     */
    ControllerResponse<Task> addTask(List<String> taskParams);

    /**
     * Updates an existing task.
     *
     * @param idx        The index of the task to update.
     * @param taskParams A list of parameters describing the updated task.
     * @return A response message confirming the task update.
     */
    ControllerResponse<Task> updateTask(int idx, TaskUpdateDao taskParams);

    /**
     * Deletes a task by its identifier.
     *
     * @param taskId The ID of the task to delete.
     * @return A response message indicating the deleted task.
     */
    ControllerResponse<Task> deleteTask(int taskId);

    /**
     * Searches for a task by its unique identifier.
     *
     * @param uuidStr The unique identifier of the task.
     * @return A response message containing the task's position in the list.
     */
    ControllerResponse<Integer> searchOrder(String uuidStr);

    /**
     * Finds a task by its position in the list.
     *
     * @param taskId The position of the task to retrieve.
     * @return A response message containing the selected task.
     */
    ControllerResponse<Task> findByOrder(int taskId);

    /**
     * Searches for tasks that contain a given keyword.
     *
     * @param keyword The keyword to filter tasks.
     * @return A formatted string listing matching tasks.
     */
    ControllerResponse<String> searchByKeyword(String keyword);

    /**
     * Searches for tasks within a specified date range and type.
     *
     * @param type The type of tasks to search for.
     * @param from The start date of the search range.
     * @param to   The end date of the search range.
     * @return A formatted string listing tasks within the given date range.
     */
    ControllerResponse<String> searchByDate(TaskType type, LocalDateTime from, LocalDateTime to);

    /**
     * Deletes all tasks in the system.
     *
     * @return A response message confirming the deletion of all tasks.
     */
    ControllerResponse<String> deleteAll();
}
