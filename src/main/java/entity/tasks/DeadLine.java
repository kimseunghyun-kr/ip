package entity.tasks;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


/**
 * Represents a deadline task with a specific due date and time.
 * <p>
 * This class extends {@link Task} and includes a {@code dueby} field
 * to indicate when the task is due.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DeadLine extends Task {

    private final LocalDateTime dueby;

    /**
     * Constructs a {@code DeadLine} task with the specified name and due date.
     *
     * @param name  The name of the deadline task.
     * @param dueby The date and time by which the task is due.
     */
    public DeadLine(String name, LocalDateTime dueby) {
        super(name);
        this.dueby = dueby;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[D] ");
        if (super.getCompleted()) {
            builder.append("[X] ");
        } else {
            builder.append("[ ] ");
        }
        builder.append(super.getName());
        builder.append(" due by  :: {");
        builder.append(dueby);
        builder.append("}         ");
        builder.append("UUID:: ");
        builder.append(super.getId());
        return builder.toString();
    }
}
