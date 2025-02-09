package entity.tasks;

import static util.DateTimeUtils.parseDateOrDateTime;

import java.util.List;

import entity.TaskType;
import exceptions.UserFacingException;
import lombok.NonNull;


public class TaskFactory {
    public static Task createTask(@NonNull TaskType type, List<String> parameters) {
        return switch (type) {
        case TODO -> {
            if (parameters.size() != 1) {
                throw new UserFacingException("Todo requires exactly 1 parameter: description");
            }
            yield new ToDo(parameters.get(0));
        }
        case DEADLINE -> {
            if (parameters.size() != 2) {
                throw new UserFacingException("Deadline requires 2 parameters: description and deadline");
            }
            yield new DeadLine(parameters.get(0), parseDateOrDateTime(parameters.get(1)));
        }
        case EVENT -> {
            if (parameters.size() != 3) {
                throw new UserFacingException("Event requires 2 parameters: description and event time");
            }
            yield new Events(parameters.get(0), parseDateOrDateTime(parameters.get(1)), parseDateOrDateTime(parameters.get(2)));
        }
        default -> throw new UserFacingException("Unknown task type: " + type);
        };
    }
}

