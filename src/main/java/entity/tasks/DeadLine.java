package entity.tasks;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DeadLine extends Task {
    @NonNull
    private final LocalDateTime dueby;

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

    public LocalDateTime getDueby() {
        return dueby;
    }
}
