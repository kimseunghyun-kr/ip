package service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entity.TaskType;
import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.TaskFactory;
import entity.tasks.ToDo;
import exceptions.UserFacingException;
import repository.IFileBackedTaskRepository;
import repository.ITaskRepository;
import service.dao.TaskUpdateDao;

/**
 * Service class that coordinates task retrieval and updates between the in-memory
 * and file-backed task repositories.
 */
public class TaskRepositoryCoordinatorService {
    private final ITaskRepository taskRepository;
    private final IFileBackedTaskRepository taskBuffer;

    /**
     * Constructs a {@code TaskRepositoryCoordinatorService} with the specified repositories.
     *
     * @param taskRepository The primary task repository for storing and retrieving tasks.
     * @param taskBuffer     The file-backed repository for persisting task changes.
     */
    public TaskRepositoryCoordinatorService(ITaskRepository taskRepository, IFileBackedTaskRepository taskBuffer) {
        this.taskRepository = taskRepository;
        this.taskBuffer = taskBuffer;
    }

    /**
     * Retrieves a task by its order index in the repository.
     *
     * @param orderIndex The 1-based index of the task.
     * @return The task found at the specified index.
     * @throws UserFacingException If no task is found at the given index.
     */
    public Task findByOrder(int orderIndex) {
        Task selectedTask = taskRepository.findByOrder(orderIndex - 1)
                .orElseThrow(() -> new UserFacingException("Task not found"));
        if (selectedTask == null) {
            throw new UserFacingException("Task not found");
        }
        return selectedTask;
    }

    /**
     * Updates an existing task with the provided changes.
     *
     * @param idx       The index of the task to be updated.
     * @param updateDao The object containing updated task attributes.
     * @return The updated task after saving to the repository.
     * @throws IllegalArgumentException If the task is not found at the given index.
     */
    public Task updateTask(int idx, TaskUpdateDao updateDao) {
        Task prevTask = this.findByOrder(idx);
        if (prevTask == null) {
            throw new IllegalArgumentException("Task not found at index: " + idx);
        }

        // Preserve UUID
        UUID prevUuid = prevTask.getId();

        // Reconstruct task with updated fields
        Task updatedTask = applyUpdates(prevTask, updateDao);
        updatedTask.setId(prevUuid);

        // Save updated task
        Task savedTask = taskRepository.save(updatedTask);

        // Mark task as dirty for buffering system
        taskBuffer.markDirty(prevUuid);

        return savedTask;
    }

    /**
     * Marks a task as dirty in the file-backed repository.
     *
     * @param uuid The UUID of the task to be marked as dirty.
     */
    void markDirty(UUID uuid) {
        taskBuffer.markDirty(uuid);
    }

    /**
     * Applies updates to an existing task, preserving its type if unchanged.
     *
     * @param existingTask The task to update.
     * @param updateDao    The object containing the updated task attributes.
     * @return The updated task instance.
     */
    private Task applyUpdates(Task existingTask, TaskUpdateDao updateDao) {
        if (isSameTaskType(existingTask, updateDao)) {
            return applyPartialUpdates(existingTask, updateDao);
        } else {
            return createNewTaskWithUuid(existingTask, updateDao);
        }
    }

    /**
     * Determines whether the updated task type is the same as the existing one.
     *
     * @param existingTask The existing task.
     * @param updateDao    The update data object containing the new task type.
     * @return {@code true} if the task type remains unchanged, otherwise {@code false}.
     */
    private boolean isSameTaskType(Task existingTask, TaskUpdateDao updateDao) {
        String existingType = existingTask.getClass().getSimpleName();
        String newType = updateDao.getTaskType();
        return newType == null || existingType.equalsIgnoreCase(newType);
    }

    /**
     * Applies partial updates to a task while maintaining its type.
     *
     * @param existingTask The task to update.
     * @param updateDao    The update object containing modified attributes.
     * @return A new instance of the task with applied updates.
     * @throws IllegalArgumentException If the task type is unsupported.
     */
    private Task applyPartialUpdates(Task existingTask, TaskUpdateDao updateDao) {
        if (existingTask instanceof Events event) {
            return new Events(
                    getUpdatedValue(updateDao.getName(), event.getName()),
                    getUpdatedValue(updateDao.getStartDate(), event.getStartat()),
                    getUpdatedValue(updateDao.getEndDate(), event.getEndby())
            );
        }
        if (existingTask instanceof DeadLine deadline) {
            return new DeadLine(
                    getUpdatedValue(updateDao.getName(), deadline.getName()),
                    getUpdatedValue(updateDao.getDueDate(), deadline.getDueby())
            );
        }
        if (existingTask instanceof ToDo todo) {
            return new ToDo(getUpdatedValue(updateDao.getName(), todo.getName()));
        }
        throw new IllegalArgumentException("Unsupported task type");
    }

    /**
     * Creates a new task instance with the existing task's UUID.
     *
     * @param existingTask The original task.
     * @param updateDao    The update object containing the new task attributes.
     * @return A new task instance with the same UUID.
     */
    private Task createNewTaskWithUuid(Task existingTask, TaskUpdateDao updateDao) {
        Task newTask = TaskFactory.createTask(TaskType.valueOf(updateDao.getTaskType().toUpperCase()),
                extractParameters(updateDao));
        newTask.setId(existingTask.getId());
        return newTask;
    }

    /**
     * Returns the updated value if it has changed; otherwise, retains the original value.
     *
     * @param newValue     The new value provided in the update.
     * @param existingValue The existing value in the task.
     * @param <T>          The type of the value being updated.
     * @return The updated value if changed, otherwise the original value.
     */
    private <T> T getUpdatedValue(T newValue, T existingValue) {
        return newValue != null ? newValue : existingValue;
    }

    /**
     * Extracts the relevant task parameters from the update object.
     *
     * @param updateDao The update data object.
     * @return A list of string representations of updated parameters.
     */
    private List<String> extractParameters(TaskUpdateDao updateDao) {
        List<String> params = new ArrayList<>();
        if (updateDao.getName() != null) {
            params.add(updateDao.getName());
        }
        if (updateDao.getStartDate() != null) {
            params.add(updateDao.getStartDate().toString());
        }
        if (updateDao.getEndDate() != null) {
            params.add(updateDao.getEndDate().toString());
        }
        if (updateDao.getDueDate() != null) {
            params.add(updateDao.getDueDate().toString());
        }
        return params;
    }
}

