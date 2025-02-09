package entity.tasks;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Represents an event-based task with a start and end time.
 * <p>
 * This class extends {@link Task} and includes time constraints for when
 * the event starts and ends.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Events extends Task {

    private final LocalDateTime startat;
    private final LocalDateTime endby;

    /**
     * Constructs an event task with the specified name, start time, and end time.
     *
     * @param name    The name of the event.
     * @param startat The date and time when the event starts.
     * @param endby   The date and time when the event ends.
     */
    public Events(String name, LocalDateTime startat, LocalDateTime endby) {
        super(name);
        this.startat = startat;
        this.endby = endby;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[E] ");
        if (super.getCompleted()) {
            builder.append("[X] ");
        } else {
            builder.append("[ ] ");
        }
        builder.append(super.getName());
        builder.append(" starting from  :: {");
        builder.append(startat);
        builder.append("} ");
        builder.append(" ending by :: {");
        builder.append(endby);
        builder.append("}         ");
        builder.append("UUID:: ");
        builder.append(super.getId());
        return builder.toString();
    }
}
