package entity.tasks;

public class ToDo extends Task {
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
