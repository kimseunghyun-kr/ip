package entity.tasks;

import static util.DateTimeUtils.parseDateOrDateTime;

import java.util.List;

import entity.TaskType;
import exceptions.UserFacingException;

/**
 * Factory class for creating task instances based on task type and parameters.
 * <p>
 * This class provides a method to dynamically instantiate different types of tasks
 * ({@link ToDo}, {@link DeadLine}, {@link Events}) based on the given task type and input parameters.
 * </p>
 */
public class TaskFactory {

    /**
     * Creates a new task instance based on the given task type and parameters.
     *
     * @param type       The {@link TaskType} of the task to create.
     * @param parameters A list of string parameters required for task creation.
     *                   <ul>
     *                       <li>{@code TODO} requires 1 parameter: description.</li>
     *                       <li>{@code DEADLINE} requires 2 parameters: description and deadline.</li>
     *                       <li>{@code EVENT} requires 3 parameters: description, start time, and end time.</li>
     *                   </ul>
     * @return A new {@link Task} instance of the specified type.
     * @throws UserFacingException If the number of parameters is incorrect or the task type is unknown.
     */
    public static Task createTask(TaskType type, List<String> parameters) {
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
            yield new Events(parameters.get(0), parseDateOrDateTime(parameters.get(1)),
                    parseDateOrDateTime(parameters.get(2)));
        }
        };
    }
}

