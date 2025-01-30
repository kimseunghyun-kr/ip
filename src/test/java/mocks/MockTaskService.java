package mocks;

import java.time.LocalDateTime;
import java.util.List;

import entity.TaskType;
import entity.tasks.Task;
import service.ITaskService;



public class MockTaskService implements ITaskService {

    @Override
    public Task markDone(int index) {
        return null;
    }

    @Override
    public Task markUndone(int index) {
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        return List.of();
    }

    @Override
    public Task addTask(List<String> taskParams) {
        return null;
    }

    @Override
    public Task deleteTask(int taskId) {
        return null;
    }

    @Override
    public List<Task> searchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        return List.of();
    }

    @Override
    public int searchOrder(String uuid) {
        return null;
    }

    @Override
    public List<Task> searchByKeyword(String keyword) {
        return List.of();
    }
}
