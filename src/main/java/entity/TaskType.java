package entity;

import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.ToDo;

public enum TaskType {
    TODO,
    DEADLINE,
    EVENT;

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
