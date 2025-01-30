package entity;

import java.time.LocalDateTime;
import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;

public class MockTaskController implements ITaskController {

    @Override
    public ControllerResponse markDone(int index) {
        return null;
    }

    @Override
    public ControllerResponse markUndone(int index) {
        return null;
    }

    @Override
    public ControllerResponse getAllTasks() {
        return null;
    }

    @Override
    public ControllerResponse addTask(List<String> taskParams) {
        return null;
    }

    @Override
    public ControllerResponse deleteTask(int taskId) {
        return null;
    }

    @Override
    public ControllerResponse searchOrder(String uuidStr) {
        return null;
    }

    @Override
    public ControllerResponse searchByKeyword(String keyword) {
        return null;
    }

    @Override
    public ControllerResponse searchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        return null;
    }
}
