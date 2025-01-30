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
 * This service interacts with the {@link ITaskRepository} to perform CRUD operations.
 */
public class TaskService implements ITaskService {
    /** Service responsible for coordinating repository interactions. */
    private final TaskRepositoryCoordinatorService taskRepositoryCoordinatorService;

    /** The repository for storing and retrieving tasks. */
    private final ITaskRepository taskRepository;

    public TaskService(TaskRepositoryCoordinatorService taskRepositoryCoordinatorService, ITaskRepository taskRepository) {
        this.taskRepositoryCoordinatorService = taskRepositoryCoordinatorService;
        this.taskRepository = taskRepository;
    }

    @Override
    public Task markDone(int index) {
        Task selectedTask = taskRepositoryCoordinatorService.findByOrder(index);
        if (!selectedTask.getCompleted()) {
            selectedTask.toggleCompleted();
            taskRepositoryCoordinatorService.markDirty(selectedTask.getId());
        }
        return selectedTask; // ✅ Return object instead of formatted string
    }

    @Override
    public Task markUndone(int index) {
        Task selectedTask = taskRepositoryCoordinatorService.findByOrder(index);
        if (selectedTask.getCompleted()) {
            selectedTask.toggleCompleted();
            taskRepositoryCoordinatorService.markDirty(selectedTask.getId());
        }
        return selectedTask; // ✅ Return object
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll(); // ✅ Return list of tasks
    }

    @Override
    public Task addTask(List<String> taskParams) {
        TaskType taskType = TaskType.valueOf(taskParams.get(0).toUpperCase());
        taskParams.remove(0);
        Task newTask = TaskFactory.createTask(taskType, taskParams);
        return taskRepository.save(newTask); // ✅ Return Task object
    }

    @Override
    public Task deleteTask(int taskId) {
        return taskRepository.deleteByOrder(taskId - 1); // ✅ Return deleted task
    }

    @Override
    public int searchOrder(String uuidStr) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            return taskRepository.findOrder(uuid); // ✅ Return integer order
        } catch (IllegalArgumentException e) {
            throw new UserFacingException("Invalid UUID format.");
        }
    }

    @Override
    public List<Task> searchByKeyword(String keyword) {
        return taskRepository.findTaskWithKeyword(keyword); // ✅ Return list of tasks
    }

    @Override
    public List<Task> searchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        return taskRepository.findAllFromWhenToWhen(type, from, to); // ✅ Return list of tasks
    }
}
