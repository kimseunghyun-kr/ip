package entity.tasks;

import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private UUID id;

    private String name;

    private Boolean isCompleted;

    public Task(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.isCompleted = false;
    }

    public Task(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.isCompleted = false;
    }

    public void toggleCompleted() {
        isCompleted = !isCompleted;
    }

    public String rename(String newName) {
        this.name = newName;
        return newName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isCompleted) {
            builder.append("[X] ");
        } else {
            builder.append("[ ] ");
        }
        builder.append(name);
        builder.append("         ");
        builder.append("UUID:: ");
        builder.append(this.id);
        return builder.toString();
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(isCompleted, task.isCompleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isCompleted);
    }
}
