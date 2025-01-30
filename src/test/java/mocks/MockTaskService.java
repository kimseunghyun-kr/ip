package mocks;

import entity.TaskType;
import service.ITaskService;
import service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

public class MockTaskService implements ITaskService {
    @Override
    public String markDone(int index) {
        return "";
    }

    @Override
    public String markUndone(int index) {
        return "";
    }

    @Override
    public String getAllTasks() {
        return "";
    }

    @Override
    public String addTask(List<String> taskParams) {
        return "";
    }

    @Override
    public String deleteTask(int taskId) {
        return "";
    }

    @Override
    public String SearchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        return "";
    }

    @Override
    public String SearchOrder(String uuid) {
        return "";
    }

    @Override
    public String SearchByKeyword(String keyword) {
        return "";
    }
}
