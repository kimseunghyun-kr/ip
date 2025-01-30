package service;

import entity.TaskType;
import entity.tasks.Task;
import entity.tasks.TaskFactory;
import exceptions.UserFacingException;
import repository.ITaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implements {@link ITaskService} to provide task management operations.
 * This service interacts with the {@link ITaskRepository} to perform CRUD operations
 * and task-related queries.
 */
public class TaskService implements ITaskService {
    /** Service responsible for coordinating repository interactions. */
    private final TaskRepositoryCoordinatorService taskRepositoryCoordinatorService;

    /** The repository for storing and retrieving tasks. */
    private final ITaskRepository taskRepository;

    /**
     * Constructs a {@code TaskService} with required dependencies.
     *
     * @param taskRepositoryCoordinatorService The service for managing repository state.
     * @param taskRepository The repository used for task persistence.
     */
    public TaskService(TaskRepositoryCoordinatorService taskRepositoryCoordinatorService, ITaskRepository taskRepository) {
        this.taskRepositoryCoordinatorService = taskRepositoryCoordinatorService;
        this.taskRepository = taskRepository;
    }

    /**
     * Marks a task as completed.
     *
     * @param index The index of the task to be marked as done.
     * @return A message indicating the task's updated status.
     */
    @Override
    public String markDone(int index) {
        Task selectedTask = taskRepositoryCoordinatorService.findByOrder(index);
        if (selectedTask.getCompleted()) {
            return "the task is already marked as done \n" + selectedTask + "  ->  " + selectedTask;
        } else {
            String response = "the task has been marked as done \n" + selectedTask;
            selectedTask.toggleCompleted();
            taskRepositoryCoordinatorService.markDirty(selectedTask.getId());
            response = response + "  ->  " + selectedTask;
            return response;
        }
    }

    /**
     * Marks a task as not completed.
     *
     * @param index The index of the task to be marked as undone.
     * @return A message indicating the task's updated status.
     */
    @Override
    public String markUndone(int index) {
        Task selectedTask = taskRepositoryCoordinatorService.findByOrder(index - 1);
        if (selectedTask.getCompleted()) {
            String response = "the task has been marked as undone \n" + selectedTask;
            selectedTask.toggleCompleted();
            taskRepositoryCoordinatorService.markDirty(selectedTask.getId());
            response = response + "  ->  " + selectedTask;
            return response;
        } else {
            return "the task is already marked as undone \n" + selectedTask + "  ->  " + selectedTask;
        }
    }

    /**
     * Retrieves all tasks.
     *
     * @return A formatted list of all tasks.
     */
    @Override
    public String getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        int counter = 1;
        StringBuilder result = new StringBuilder();
        for(Task word : tasks) {
            result.append(counter).append(".").append(word).append("\n");
            counter++;
        }
        return result.toString();
    }

    /**
     * Adds a new task.
     *
     * @param taskParams A list of parameters describing the task.
     *                   The first element represents the task type.
     * @return A confirmation message.
     */
    @Override
    public String addTask(List<String> taskParams) {
        TaskType taskType = TaskType.valueOf(taskParams.get(0).toUpperCase());
        taskParams.remove(0);
        Task newTask = TaskFactory.createTask(taskType, taskParams);
        Task response = taskRepository.save(newTask);

        String header = "Nice! I have added this task \n added: ";
        return header + response + "\n";
    }

    /**
     * Deletes a task by its identifier.
     *
     * @param taskId The ID of the task to delete.
     * @return A confirmation message including the remaining task count.
     */
    @Override
    public String deleteTask(int taskId) {
        Task deleted = taskRepository.deleteByOrder(taskId-1);
        Integer remainingTasks = taskRepository.remainingTasks();
        StringBuilder result = new StringBuilder();
        result.append("the following task has been deleted\n")
                .append(deleted)
                .append("\n you have ")
                .append(remainingTasks)
                .append(" tasks in the list");

        return result.toString();
    }

    /**
     * Searches for a task by its unique identifier.
     *
     * @param uuidstr The unique identifier of the task.
     * @return The order of the task in the task list.
     * @throws UserFacingException if the provided string is not a valid UUID.
     */
    @Override
    public String searchOrder(String uuidstr) {
        try{
            UUID uuid = UUID.fromString(uuidstr);
            return Integer.valueOf(taskRepository.findOrder(uuid)).toString();
        } catch (IllegalArgumentException e) {
            throw new UserFacingException("the uid input is not a valid UUID");
        }
    }

    /**
     * Searches for tasks that contain a given keyword.
     *
     * @param keyword The keyword to filter tasks.
     * @return A formatted list of matching tasks.
     */
    @Override
    public String searchByKeyword(String keyword) {
        List<Task> taskContainingKeyword = taskRepository.findTaskWithKeyword(keyword);
        StringBuilder sb = new StringBuilder();
        sb.append("The following tasks have been searched\n");
        for(Task containingKeyword : taskContainingKeyword) {
            sb.append(containingKeyword).append("\n");
        }
        return sb.toString();

    }

    /**
     * Searches for tasks within a specified date range and type.
     *
     * @param type The type of tasks to search for.
     * @param from The start date of the search range.
     * @param to   The end date of the search range.
     * @return A formatted list of tasks within the given date range.
     */
    @Override
    public String searchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        List<Task> withinDates = taskRepository.findAllFromWhenToWhen(type, from, to);
        StringBuilder sb = new StringBuilder();
        sb.append("The following tasks have been searched\n");
        for(Task withinDate : withinDates) {
            sb.append(withinDate).append("\n");
        }
        return sb.toString();
    }
}
