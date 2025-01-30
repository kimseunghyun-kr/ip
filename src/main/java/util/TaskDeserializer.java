package util;

import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.ToDo;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskDeserializer {
    public static Task deserializeTask(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 4) return null; // Invalid format

        try {
            UUID id = UUID.fromString(parts[0]); // Extract UUID
            String type = parts[1];
            boolean isCompleted = parts[2].equals("1");
            String name = parts[3];

            Task task;

            switch (type) {
                case "T":
                    task = new ToDo(name);
                    break;

                case "D":
                    if (parts.length < 5) return null;
                    LocalDateTime dueby = LocalDateTime.parse(parts[4]);
                    task = new DeadLine(name, dueby);
                    break;

                case "E":
                    if (parts.length < 6) return null;
                    LocalDateTime startat = LocalDateTime.parse(parts[4]);
                    LocalDateTime endby = LocalDateTime.parse(parts[5]);
                    task = new Events(name, startat, endby);
                    break;

                default:
                    return null; // Unknown task type
            }

            task.setCompleted(isCompleted);
            task.setId(id); // Assign UUID from file
            return task;

        } catch (Exception e) {
            System.err.println("Error parsing task: " + line);
            return null; // Invalid format
        }
    }
}
