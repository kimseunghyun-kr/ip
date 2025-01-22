package entity.tasks;

import java.time.LocalDateTime;

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
        return builder.toString();
    }
}
