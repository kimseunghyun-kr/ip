package repository.event;

import java.util.UUID;

import entity.tasks.Task;
import lombok.Getter;

/**
 * Represents an event related to task operations.
 * <p>
 * This class acts as a carrier for event-based task updates, deletions, or additions.
 * It holds event details including the event type, associated task, and task UUID.
 * </p>
 */
@Getter
public class TaskEvent {
    private final EventType type;
    private final Task task;
    private final UUID taskId;
    /**
     * Constructs a task event with the specified type and task.
     *
     * @param type The type of event being recorded.
     * @param task The task associated with the event.
     */
    public TaskEvent(EventType type, Task task) {
        this.type = type;
        this.task = task;
        this.taskId = (task != null) ? task.getId() : null;
    }
    /**
     * Constructs a task event with the specified type and task ID.
     *
     * @param type   The type of event being recorded.
     * @param taskId The UUID of the task associated with the event.
     */
    public TaskEvent(EventType type, UUID taskId) {
        this.type = type;
        this.task = null;
        this.taskId = taskId;
    }
    /**
     * Represents the types of events that can occur on a task.
     */
    public enum EventType {
        ADD, UPDATE, DELETE, DELETEALL
    }
}

