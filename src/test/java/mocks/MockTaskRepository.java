package mocks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import entity.TaskType;
import entity.tasks.Task;
import repository.IFileBackedTaskRepository;
import repository.ITaskRepository;


public class MockTaskRepository implements ITaskRepository, IFileBackedTaskRepository {

    List<Task> temptaskStore = new ArrayList<>();

    @Override
    public void flush() {
    }

    @Override
    public UUID markDirty(UUID id) {
        return temptaskStore.get(0).getId();
    }

    @Override
    public Task save(Task entity) {
        temptaskStore.add(entity);
        return entity;
    }

    @Override
    public Optional<Task> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Task> findByOrder(Integer id) {
        return Optional.ofNullable(temptaskStore.get(id));
    }

    @Override
    public List<Task> findAll() {
        return temptaskStore;
    }

    @Override
    public Task deleteById(UUID uuid) {
        return null;
    }

    @Override
    public Task deleteByOrder(Integer id) {
        Task task = temptaskStore.get(id);
        temptaskStore.remove((int) id);
        return task;
    }

    @Override
    public Integer remainingTasks() {
        return temptaskStore.size();
    }

    @Override
    public List<Task> findAllFromWhenToWhen(TaskType type, LocalDateTime from, LocalDateTime to) {
        return temptaskStore;
    }

    @Override
    public int findOrder(UUID uuid) {
        return 0;
    }

    @Override
    public List<Task> findTaskWithKeyword(String keyword) {
        return temptaskStore;
    }

    @Override
    public List<Task> deleteAll() {
        return List.of();
    }
}
