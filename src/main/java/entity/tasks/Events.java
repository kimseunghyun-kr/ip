package entity.tasks;

import java.time.LocalDateTime;


public class Events extends Task {

    private LocalDateTime startat;
    private LocalDateTime endby;
    public Events(String name, LocalDateTime startat, LocalDateTime endby) {
        super(name);
        this.startat = startat;
        this.endby = endby;
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

    public LocalDateTime getEndby() {
        return endby;
    }

    public LocalDateTime getStartat() {
        return startat;
    }
}
