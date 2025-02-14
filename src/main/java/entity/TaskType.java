package entity;

import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.ToDo;

/**
 * Enum representing different types of tasks.
 */
public enum TaskType {
    TODO,
    DEADLINE,
    EVENT;

    /**
     * Determines the {@code TaskType} based on the given task instance.
     *
     * @param task The task whose type is to be identified.
     * @return The corresponding {@code TaskType}.
     * @throws IllegalArgumentException If the task type is unrecognized.
     */
    public static TaskType fromTask(Task task) {
        if (task instanceof ToDo) {
            return TODO;
        } else if (task instanceof DeadLine) {
            return DEADLINE;
        } else if (task instanceof Events) {
            return EVENT;
        }
        throw new IllegalArgumentException("Unknown task type: " + task.getClass().getSimpleName());
    }
}
