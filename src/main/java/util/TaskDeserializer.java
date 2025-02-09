package util;

import java.time.LocalDateTime;
import java.util.UUID;

import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.ToDo;

/**
 * Utility class for deserializing task objects from string representations.
 *
 * <p>
 * This class converts a serialized task string into a {@link Task} object by parsing
 * its components such as UUID, type, completion status, name, and timestamps.
 * </p>
 */
public class TaskDeserializer {

    /**
     * Deserializes a task from a formatted string.
     *
     * <p>Expected format: {@code UUID|TYPE|COMPLETED|NAME|[OPTIONAL_TIMESTAMPS]}</p>
     * <ul>
     *     <li>{@code UUID} - The unique identifier of the task.</li>
     *     <li>{@code TYPE} - "T" (ToDo), "D" (Deadline), "E" (Event).</li>
     *     <li>{@code COMPLETED} - "1" for completed, "0" for incomplete.</li>
     *     <li>{@code NAME} - The task name or description.</li>
     *     <li>{@code OPTIONAL_TIMESTAMPS} -
     *         <ul>
     *             <li>For Deadline: {@code dueBy}</li>
     *             <li>For Event: {@code startAt | endBy}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param line The serialized task string.
     * @return A {@link Task} object if the string is valid; otherwise, throws an exception.
     * @throws IllegalArgumentException If the input format is invalid.
     */
    public static Task deserializeTask(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid task format: " + line);
        }

        try {
            UUID id = UUID.fromString(parts[0]); // Extract UUID
            String type = parts[1];
            boolean isCompleted = parts[2].equals("1");
            String name = parts[3];

            return switch (type) {
            case "T" -> ToDo.builder().id(id).name(name).isCompleted(isCompleted).build();
            case "D" -> {
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Deadline task missing dueBy timestamp: " + line);
                }
                LocalDateTime dueBy = LocalDateTime.parse(parts[4]);
                yield DeadLine.builder().id(id).name(name).dueby(dueBy).isCompleted(isCompleted).build();
            }
            case "E" -> {
                if (parts.length < 6) {
                    throw new IllegalArgumentException("Event task missing startAt or endBy timestamps: " + line);
                }
                LocalDateTime startAt = LocalDateTime.parse(parts[4]);
                LocalDateTime endBy = LocalDateTime.parse(parts[5]);
                yield Events.builder().id(id).name(name).startat(startAt).endby(endBy)
                        .isCompleted(isCompleted).build();
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
            };

        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing task: " + line, e);
        }
    }
}
