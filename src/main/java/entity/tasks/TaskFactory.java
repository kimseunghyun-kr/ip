package entity.tasks;

import entity.TaskType;

import java.time.LocalDateTime;
import java.util.List;

import static util.DateTimeUtils.parseDateOrDateTime;

public class TaskFactory {
    public static Task createTask(TaskType type, List<String> parameters) {
        return switch (type) {
            case TaskType.TODO -> {
                if (parameters.size() != 1) {
                    throw new IllegalArgumentException("Todo requires exactly 1 parameter: description");
                }
                yield new ToDo(parameters.get(0));
            }
            case TaskType.DEADLINE -> {
                if (parameters.size() != 2) {
                    throw new IllegalArgumentException("Deadline requires 2 parameters: description and deadline");
                }
                yield new DeadLine(parameters.get(0), parseDateOrDateTime(parameters.get(1)));
            }
            case TaskType.EVENT -> {
                if (parameters.size() != 3) {
                    throw new IllegalArgumentException("Event requires 2 parameters: description and event time");
                }
                yield new Events(parameters.get(0), parseDateOrDateTime(parameters.get(1)), parseDateOrDateTime(parameters.get(2)));
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };
    }
}

