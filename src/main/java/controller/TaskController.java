package controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import entity.TaskType;
import entity.tasks.Task;
import exceptions.UserFacingException;
import service.ITaskService;


/**
 * TaskController formats responses for user interaction.
 * Handles communication between user input (CLI/GUI) and TaskService.
 */
public class TaskController implements ITaskController {
    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public ControllerResponse markDone(int index) {
        Task updatedTask = taskService.markDone(index);
        return new ControllerResponse("Task marked as done: \n " + updatedTask);
    }

    @Override
    public ControllerResponse markUndone(int index) {
        Task updatedTask = taskService.markUndone(index);
        return new ControllerResponse("Task marked as undone: \n " + updatedTask);
    }

    @Override
    public ControllerResponse getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return formatTaskList(tasks);
    }

    @Override
    public ControllerResponse addTask(List<String> taskParams) {
        Task newTask = taskService.addTask(taskParams);
        return new ControllerResponse("Task added successfully:\n" + newTask);
    }

    @Override
    public ControllerResponse deleteTask(int taskId) {
        Task deletedTask = taskService.deleteTask(taskId);
        return new ControllerResponse("Deleted task:\n" + deletedTask);
    }

    @Override
    public ControllerResponse searchOrder(String uuidStr) {
        try {
            int order = taskService.searchOrder(uuidStr);
            return new ControllerResponse("Task found at position: " + order);
        } catch (UserFacingException e) {
            return new ControllerResponse(e.getMessage());
        }
    }

    @Override
    public ControllerResponse searchByKeyword(String keyword) {
        List<Task> tasks = taskService.searchByKeyword(keyword);
        return new ControllerResponse("Tasks containing '" + keyword + "':\n", formatTaskList(tasks));
    }

    @Override
    public ControllerResponse searchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        List<Task> tasks = taskService.searchByDate(type, from, to);
        return new ControllerResponse("Tasks from " + from + " to " + to + ":\n", formatTaskList(tasks));
    }

    @Override
    public ControllerResponse deleteAll() {
        List<Task> deletedTasks = taskService.deleteAll();
        return new ControllerResponse("Deleted all tasks!", formatTaskList(deletedTasks));
    }

    private ControllerResponse formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return new ControllerResponse("No tasks found.");
        }
        return new ControllerResponse(IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i))
                .collect(Collectors.joining("\n")));
    }
}
