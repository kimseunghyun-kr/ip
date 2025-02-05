package repository.event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import entity.tasks.Task;
import exceptions.UserFacingException;
import util.DataFileUtils;
import util.TaskDeserializer;
import util.TaskSerializer;

/**
 * Handles logging and replaying of task-related events.
 */
public class TaskEventLogger {
    private final Path logFilePath;
    /**
     * Constructs a TaskEventLogger and registers it for event handling.
     *
     * @param logFilePath The file path where task events are logged.
     */
    public TaskEventLogger(Path logFilePath) {
        this.logFilePath = logFilePath;
        TaskEventObject.getInstance().register(this::handleEvent);
    }

    /**
     * Handles task events and logs them appropriately.
     *
     * @param event The task event to be logged.
     */
    private synchronized void handleEvent(TaskEvent event) {
        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            switch (event.getType()) {
            case ADD, UPDATE -> writer.write(event.getType() + " "
                    + TaskSerializer.serializeTask(event.getTask()) + "\n");
            case DELETE -> writer.write("DELETE " + event.getTaskId() + "\n");
            default -> throw new UserFacingException("Unknown event type: " + event.getType());
            }
        } catch (IOException e) {
            System.err.println("Error logging task event: " + e.getMessage());
        }
    }
    /**
     * Replays the log to update the task storage file.
     *
     * @param filePath The file where tasks are stored.
     */
    public synchronized void replayLog(Path filePath) {
        if (!Files.exists(logFilePath)) {
            return; // No logs to apply
        }

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
                    readLogLine(storageMap, logLine);
                }
            } catch (IOException e) {
                System.err.println("Error reading log file: " + e.getMessage());
                return;
            }

            // Write the updated tasks to the temporary file with the correct format
            writer.write("[\n"); // Write opening bracket
            for (Task task : storageMap.values()) {
                writer.write(TaskSerializer.serializeTask(task) + "\n");
            }
            writer.write("]\n"); // Write closing bracket

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
     * Parses a log line and updates the provided task storage map accordingly.
     * <p>
     * This method processes log entries in the format:
     * <pre>{@code
     * EVENT_TYPE task_data
     * }</pre>
     * where {@code EVENT_TYPE} can be {@code ADD}, {@code UPDATE}, or {@code DELETE}.
     * <ul>
     *     <li>{@code ADD} and {@code UPDATE}: Deserialize the task and store/update it in the map.</li>
     *     <li>{@code DELETE}: Remove the task identified by its UUID.</li>
     * </ul>
     * If the log entry is invalid or contains an unknown event type, it is skipped or an exception is thrown.
     *
     * @param storageMap A map storing tasks, indexed by their UUIDs.
     * @param logLine    A string containing the log entry to process.
     * @throws UserFacingException If an unknown event type is encountered.
     */
    private void readLogLine(Map<UUID, Task> storageMap, String logLine) {
        String[] parts = logLine.split(" ", 2);
        if (parts.length < 2) {
            return;
        }

        TaskEvent.EventType eventType;
        try {
            eventType = TaskEvent.EventType.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            System.err.println("Skipping invalid log entry: " + logLine);
            return;
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
        default -> throw new UserFacingException("Unknown event type: " + eventType);
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
        if (!Files.exists(logFilePath)) {
            Objects.hash(testList.toArray()); // Order-sensitive; Return unchanged if no log
        }

        List<Task> replayedList = new ArrayList<>(testList);
        Map<UUID, Task> tempStorage = new LinkedHashMap<>();
        for (Task task : replayedList) {
            tempStorage.put(task.getId(), task);
        }

        try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                readLogLine(tempStorage, line);
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
}
