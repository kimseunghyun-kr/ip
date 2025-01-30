package entity.tasks;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeadLine extends Task {

    private LocalDateTime dueby;

    public DeadLine(String name, LocalDateTime dueby) {
        super(name);
        this.dueby = dueby;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[D] ");
        if(super.getCompleted()){
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
