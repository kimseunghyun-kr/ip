package entity.tasks;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Represents a simple to-do task.
 * <p>
 * This class extends {@code Task} and provides a textual representation
 * of the task, including its completion status and unique identifier.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ToDo extends Task {
    /**
     * Constructs a {@code ToDo} task with the specified name.
     *
     * @param name The name of the to-do task.
     */
    public ToDo(String name) {
        super(name);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[T] ");
        if (super.getCompleted()) {
            builder.append("[X] ");
        } else {
            builder.append("[ ] ");
        }
        builder.append(super.getName());
        builder.append("         ");
        builder.append("UUID:: ");
        builder.append(super.getId());
        return builder.toString();
    }
}
