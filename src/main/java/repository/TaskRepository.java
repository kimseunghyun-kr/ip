package repository;

import entity.tasks.Task;
import exceptions.UserFacingException;

import java.util.*;

public class TaskRepository implements ITaskRepository {
    protected final List<Task> storageList = new ArrayList<>();
    protected final Map<UUID, Task> storageMap = new LinkedHashMap<>(); // Fast lookup by UUID

    @Override
    public Task save(Task input) {
        if (input.getId() == null) {
            input = new Task(input.getName()); // Ensure Task has UUID
        }

        storageList.add(input); // Maintain order
        storageMap.put(input.getId(), input); // Fast UUID lookup
        return input;
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storageList);
    }

    @Override
    public Optional<Task> findById(Integer index) {
        if (index < 0 || index >= storageList.size()) {
            throw new UserFacingException("Index " + (index + 1) + " is out of bounds (1 - " + storageList.size() + ")");
        }
        return Optional.ofNullable(storageList.get(index));
    }

    @Override
    public Task deleteById(Integer index) {
        if (index < 0 || index >= storageList.size()) {
            throw new UserFacingException("Index " + (index + 1) + " is out of bounds (1 - " + storageList.size() + ")");
        }

        Task task = storageList.get(index);
        storageList.remove(index); // Maintain list order
        storageMap.remove(task.getId()); // Remove from fast lookup
        return task;
    }

    @Override
    public Integer remainingTasks() {
        return storageList.size();
    }
}
