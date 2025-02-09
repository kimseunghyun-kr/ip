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


public class TaskRepositoryCoordinatorService {
    private final ITaskRepository taskRepository;
    private final IFileBackedTaskRepository taskBuffer;

    public TaskRepositoryCoordinatorService(ITaskRepository taskRepository, IFileBackedTaskRepository taskBuffer) {
        this.taskRepository = taskRepository;
        this.taskBuffer = taskBuffer;
    }

    public Task findByOrder(int orderIndex) {
        Task selectedTask = taskRepository.findByOrder(orderIndex - 1)
                .orElseThrow(() -> new UserFacingException("Task not found"));
        if (selectedTask == null) {
            throw new UserFacingException("Task not found");
        }
        return selectedTask;
    }

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


    void markDirty(UUID uuid) {
        taskBuffer.markDirty(uuid);
    }

    private Task applyUpdates(Task existingTask, TaskUpdateDao updateDao) {
        if (isSameTaskType(existingTask, updateDao)) {
            return applyPartialUpdates(existingTask, updateDao);
        } else {
            return createNewTaskWithUuid(existingTask, updateDao);
        }
    }

    private boolean isSameTaskType(Task existingTask, TaskUpdateDao updateDao) {
        String existingType = existingTask.getClass().getSimpleName();
        String newType = updateDao.getTaskType();
        return newType == null || existingType.equalsIgnoreCase(newType);
    }

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

    private Task createNewTaskWithUuid(Task existingTask, TaskUpdateDao updateDao) {
        Task newTask = TaskFactory.createTask(TaskType.valueOf(updateDao.getTaskType().toUpperCase()),
                extractParameters(updateDao));
        newTask.setId(existingTask.getId());
        return newTask;
    }

    private <T> T getUpdatedValue(T newValue, T existingValue) {
        return newValue != null ? newValue : existingValue;
    }

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

