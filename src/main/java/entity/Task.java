package entity;

public class Task {
    private String name;
    private Boolean isCompleted;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(isCompleted){
            builder.append("[X] ");
        } else {
            builder.append("[ ] ");
        }
        builder.append(name);
        return builder.toString();
    }

}
