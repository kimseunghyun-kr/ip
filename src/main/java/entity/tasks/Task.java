package entity.tasks;


public class Task {

    private String name;
    private Boolean isCompleted;

    public Task(String name) {
        this.name = name;
        this.isCompleted = false;
    }

    public void toggleCompleted() {
        isCompleted = !isCompleted;
    }

    public String rename(String newName){
        this.name = newName;
        return newName;
    }

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

    public String getName() {
        return name;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }
}
