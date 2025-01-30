package mocks;

import java.time.LocalDateTime;
import java.util.List;

import entity.TaskType;
import service.ITaskService;



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
    public String searchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        return "";
    }

    @Override
    public String searchOrder(String uuid) {
        return "";
    }

    @Override
    public String searchByKeyword(String keyword) {
        return "";
    }
}
