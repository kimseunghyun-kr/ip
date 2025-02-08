package service;

import java.util.UUID;

import entity.tasks.Task;
import exceptions.UserFacingException;
import repository.IFileBackedTaskRepository;
import repository.ITaskRepository;


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


    void markDirty(UUID uuid) {
        taskBuffer.markDirty(uuid);
    }

}

