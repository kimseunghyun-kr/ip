package entity.tasks;

import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents a task entity with an ID, name, and completion status.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    /**
     * Unique identifier for the task.
     */
    private UUID id;

    /**
     * Name of the task.
     */
    private String name;

    /**
     * Status indicating whether the task is completed.
     */
    private Boolean isCompleted;

    /**
     * Constructs a new task with a randomly generated UUID and a given name.
     * The task is initialized as not completed.
     *
     * @param name The name of the task.
     */
    public Task(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.isCompleted = false;
    }

    /**
     * Constructs a new task with a specified UUID and name.
     * The task is initialized as not completed.
     *
     * @param id The unique identifier for the task.
     * @param name The name of the task.
     */
    public Task(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.isCompleted = false;
    }

    /**
     * Toggles the completion status of the task.
     */
    public void toggleCompleted() {
        isCompleted = !isCompleted;
    }

    /**
     * Retrieves the completion status of the task.
     *
     * @return true if the task is completed, false otherwise.
     */
    public Boolean getCompleted() {
        return isCompleted;
    }

    /**
     * Provides a string representation of the task, including its completion status and UUID.
     *
     * @return A formatted string representing the task.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isCompleted) {
            builder.append("[X] ");
        } else {
            builder.append("[ ] ");
        }
        builder.append(name);
        builder.append("         ");
        builder.append("UUID:: ");
        builder.append(this.id);
        return builder.toString();
    }

    /**
     * Checks if this task is equal to another object.
     *
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name)
                && Objects.equals(isCompleted, task.isCompleted);
    }

    /**
     * Generates a hash code for the task based on its ID, name, and completion status.
     *
     * @return The hash code of the task.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, isCompleted);
    }
}
