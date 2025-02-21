package repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import entity.TaskType;
import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import exceptions.UserFacingException;

/**
 * In-memory repository for managing tasks.
 * <p>
 * This repository provides storage and lookup functionality for tasks,
 * supporting both list-based and map-based storage mechanisms.
 * </p>
 */
public class TaskRepository implements ITaskRepository {

    /**
     * List-based storage for tasks, maintaining insertion order.
     */
    protected final List<Task> storageList = new ArrayList<>();

    /**
     * Map-based storage for fast lookup of tasks by their UUID.
     */
    protected final Map<UUID, Task> storageMap = new LinkedHashMap<>();

    @Override
    public Task save(Task input) {
        if (storageMap.containsKey(input.getId())) {
            storageList.replaceAll(task -> task.getId().equals(input.getId()) ? input : task);
            storageMap.replace(input.getId(), input);
        } else {
            storageList.add(input); // Maintain order
            storageMap.put(input.getId(), input); // Fast UUID lookup
        }
        return input;
    }

    @Override
    public Optional<Task> findById(UUID uuid) {
        return Optional.ofNullable(storageMap.get(uuid));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storageList);
    }

    @Override
    public Task deleteById(UUID uuid) {
        Task task = storageMap.remove(uuid);
        storageList.remove(task);
        return task;
    }

    @Override
    public Optional<Task> findByOrder(Integer index) {
        if (index < 0 || index >= storageList.size()) {
            throw new UserFacingException("Index " + (index + 1)
                    + " is out of bounds (1 - " + storageList.size() + ")");
        }
        return Optional.ofNullable(storageList.get(index));
    }

    @Override
    public Task deleteByOrder(Integer index) {
        if (index < 0 || index >= storageList.size()) {
            throw new UserFacingException("Index " + (index + 1)
                    + " is out of bounds (1 - " + storageList.size() + ")");
        }

        Task task = storageList.get(index);
        storageList.remove((int) index); // Maintain list order
        storageMap.remove(task.getId()); // Remove from fast lookup
        return task;
    }

    @Override
    public Integer remainingTasks() {
        return storageList.size();
    }

    @Override
    public List<Task> findAllFromWhenToWhen(TaskType type, LocalDateTime from, LocalDateTime to) {
        return storageList.stream()
                .filter(task -> type.equals(TaskType.fromTask(task))) // Filter by type first
                .filter(task -> {
                    if (task instanceof Events events) {
                        boolean afterFrom = (from == null || events.getStartat().isAfter(from)
                                || events.getStartat().isBefore(from));
                        boolean beforeTo = (to == null || events.getEndby().isBefore(to));
                        return afterFrom && beforeTo;
                    } else if (task instanceof DeadLine deadLine) {
                        boolean afterFrom = (from == null || deadLine.getDueby().isAfter(from)
                                || deadLine.getDueby().isEqual(from));
                        boolean beforeTo = (to == null || deadLine.getDueby().isBefore(to));
                        return afterFrom && beforeTo;
                    }
                    return false;
                })
                .toList();
    }

    @Override
    public int findOrder(UUID uuid) {
        int k = storageList.indexOf(storageMap.get(uuid));
        if (k == -1) {
            throw new UserFacingException("No task found for uuid: " + uuid);
        }
        return k;
    }

    @Override
    public List<Task> findTaskWithKeyword(String keyword) {
        return storageList.stream().filter(entry -> entry.getName().contains(keyword)).toList();
    }

    @Override
    public List<Task> deleteAll() {
        List<Task> result = new ArrayList<>(this.storageList);
        storageList.clear();
        storageMap.clear();
        return result;
    }

}
