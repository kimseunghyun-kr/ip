package repository.event;

import java.util.UUID;

import entity.tasks.Task;

public class TaskEvent {
    private final EventType type;
    private final Task task;
    private final UUID taskId;
    public TaskEvent(EventType type, Task task) {
        this.type = type;
        this.task = task;
        this.taskId = (task != null) ? task.getId() : null;
    }

    public TaskEvent(EventType type, UUID taskId) {
        this.type = type;
        this.task = null;
        this.taskId = taskId;
    }

    public EventType getType() {
        return type;
    }

    public Task getTask() {
        return task;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public enum EventType {ADD, UPDATE, DELETE, DELETEALL}
}

