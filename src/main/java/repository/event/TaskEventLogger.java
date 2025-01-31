package repository.event;

import entity.tasks.Task;
import util.DataFileUtils;
import util.TaskDeserializer;
import util.TaskSerializer;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TaskEventLogger {
    private final Path logFilePath;
    private static final String TEMP_LOG_FILE_SUFFIX = ".tmp";

    public TaskEventLogger(Path logFilePath) {
        this.logFilePath = logFilePath;
        TaskEventObject.getInstance().register(this::handleEvent);
    }

    private synchronized void handleEvent(TaskEvent event) {
        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            switch (event.getType()) {
            case ADD, UPDATE -> writer.write(event.getType() + " " + TaskSerializer.serializeTask(event.getTask()) + "\n");
            case DELETE -> writer.write("DELETE " + event.getTaskId() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error logging task event: " + e.getMessage());
        }
    }

    public synchronized void replayLog(Path filePath) {
        if (!Files.exists(logFilePath)) return; // No logs to apply

        Path tempFilePath = Paths.get(filePath.toString() + ".tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            // Load existing tasks from file using FileUtils
            Map<UUID, Task> storageMap = new LinkedHashMap<>();
            try {
                storageMap.putAll(DataFileUtils.readTasksFromFile(filePath));
            } catch (IOException e) {
                System.err.println("Error loading existing tasks: " + e.getMessage());
            }

            // Read and apply logs
            try {
                List<String> logLines = DataFileUtils.readNonEmptyLines(logFilePath);
                for (String logLine : logLines) {
                    String[] parts = logLine.split(" ", 2);
                    if (parts.length < 2) continue;  // Ignore corrupt lines

                    TaskEvent.EventType eventType;
                    try {
                        eventType = TaskEvent.EventType.valueOf(parts[0]);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid log entry: " + logLine);
                        continue;
                    }

                    switch (eventType) {
                    case ADD, UPDATE -> {
                        Task task = TaskDeserializer.deserializeTask(parts[1]);
                        storageMap.put(task.getId(), task);
                    }
                    case DELETE -> {
                        UUID taskId = UUID.fromString(parts[1]);
                        storageMap.remove(taskId);
                    }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading log file: " + e.getMessage());
                return;
            }

            // Write updated tasks back to file
            for (Task task : storageMap.values()) {
                writer.write(TaskSerializer.serializeTask(task) + "\n");
            }

        } catch (IOException e) {
            System.err.println("Error applying logs to file: " + e.getMessage());
            return;
        }

        // Replace original file with updated file
        try {
            Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);
            clearLog(); // Clear logs after successful application
        } catch (IOException e) {
            System.err.println("Error replacing original file with updated file: " + e.getMessage());
        }
    }



    /**
     * Applies log replay to a given list without modifying actual storage.
     * This allows testing how logs would affect the state without applying them.
     *
     * @param testList A copy of the task list where logs will be applied.
     * @return The modified list after log replay.
     */
    public int tryLogReplay(List<Task> testList) {
        if (!Files.exists(logFilePath)) Objects.hash(testList.toArray()); // Order-sensitive; // Return unchanged if no log

        List<Task> replayedList = new ArrayList<>(testList);
        Map<UUID, Task> tempStorage = new LinkedHashMap<>();
        for (Task task : replayedList) {
            tempStorage.put(task.getId(), task);
        }

        try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                if (parts.length < 2) continue;  // Ignore corrupt lines

                TaskEvent.EventType eventType;
                try {
                    eventType = TaskEvent.EventType.valueOf(parts[0]);
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid log entry: " + line);
                    continue;
                }

                switch (eventType) {
                case ADD, UPDATE -> {
                    Task task = TaskDeserializer.deserializeTask(parts[1]);
                    tempStorage.put(task.getId(), task);
                }
                case DELETE -> {
                    UUID taskId = UUID.fromString(parts[1]);
                    tempStorage.remove(taskId);
                }
                }
            }
        } catch (IOException e) {
            System.err.println("Error during tryLogReplay: " + e.getMessage());
        }

        // Convert map back to list
        replayedList.clear();
        replayedList.addAll(tempStorage.values());
        return Objects.hash(replayedList.toArray()); // Order-sensitive
    }

    /**
     * Clears the log file after log replay has been applied.
     */
    public synchronized void clearLog() {
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            System.err.println("Error clearing log: " + e.getMessage());
        }
    }


    private void rewriteLog(Collection<Task> tasks) {
        Path tempLogFilePath = Paths.get(logFilePath.toString() + TEMP_LOG_FILE_SUFFIX);

        try (BufferedWriter writer = Files.newBufferedWriter(tempLogFilePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Task task : tasks) {
                writer.write("ADD " + TaskSerializer.serializeTask(task) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error rewriting log file: " + e.getMessage());
            return;
        }

        try {
            Files.move(tempLogFilePath, logFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error replacing log file: " + e.getMessage());
        }
    }

}
