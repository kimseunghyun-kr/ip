package entity.tasks;

import java.time.LocalDateTime;

public class Events extends Task {
    private LocalDateTime startat;
    private LocalDateTime endby;
    public Events(String name, LocalDateTime startat, LocalDateTime endby) {
        super(name);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[E] ");
        if(super.getCompleted()){
            builder.append("[X] ");
        } else {
            builder.append("[ ] ");
        }
        builder.append(super.getName());
        return builder.toString();
    }
}
