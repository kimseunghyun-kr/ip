package repository;

import static util.TaskDeserializer.deserializeTask;
import static util.TaskSerializer.serializeTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import entity.tasks.Task;
import repository.event.TaskEvent;
import repository.event.TaskEventLogger;
import repository.event.TaskEventObject;
import util.DataFileUtils;




/**
 * A file-backed implementation of {@link IFileBackedTaskRepository}.
 * This repository persists tasks to disk and manages buffered writes to reduce I/O operations.
 *
 * <p>
 * Features:
 * <ul>
 *     <li>Uses a <b>dirty tracking system</b> to minimize unnecessary writes.</li>
 *     <li>Flushes changes periodically via explicit calls or scheduled intervals.</li>
 *     <li>Implements <b>backup and recovery</b> mechanisms to prevent data loss.</li>
 * </ul>
 * </p>
 */
public class FileBackedTaskRepository extends TaskRepository implements IFileBackedTaskRepository {

    private final Path filePath;
    private final TaskEventLogger eventLogger;
    private final Set<UUID> dirtySet = new HashSet<>(); // Tracks modified tasks

    /**
     * Constructs a {@code FileBackedTaskRepository} and loads existing tasks from the specified file.
     *
     * @param filePath The file path where tasks will be persisted.
     */
    public FileBackedTaskRepository(Path filePath, TaskEventLogger eventLogger) {
        this.filePath = filePath;
        this.eventLogger = eventLogger;
        loadFromFile(false);

        // Replay log to update the state
        eventLogger.clearLog();
    }

    /**
     * Saves a task and marks it as modified.
     *
     * @param entity The task to save.
     * @return The saved task.
     */
    @Override
    public Task save(Task entity) {
        Task result = super.save(entity);
        dirtySet.add(result.getId());
        TaskEventObject.getInstance().dispatch(new TaskEvent(TaskEvent.EventType.ADD, result));
        return result;
    }

    /**
     * Deletes a task by its order index and marks it for persistence.
     *
     * @param index The order index of the task to delete.
     * @return The deleted task, or {@code null} if not found.
     */
    @Override
    public Task deleteByOrder(Integer index) {
        Task task = super.deleteByOrder(index);
        if (task != null) {
            dirtySet.add(task.getId());
            TaskEventObject.getInstance().dispatch(new TaskEvent(TaskEvent.EventType.DELETE, task.getId()));
        }
        return task;
    }

    /**
     * Deletes all tasks and marks it for persistence.
     */
    @Override
    public List<Task> deleteAll() {
        List<Task> deleted = super.deleteAll();
        deleted.forEach(task -> {
            dirtySet.add(task.getId());
            TaskEventObject.getInstance()
                .dispatch(new TaskEvent(TaskEvent.EventType.DELETE, task.getId()));
        });
        return deleted;
    }

    /**
     * Flushes all modified tasks to disk.
     * If no changes were made, this operation is skipped.
     */
    @Override
    public void flush() {
        if (dirtySet.isEmpty()) return;

        System.out.println("Flushing modified tasks to file...");

        // Step 1: Load a snapshot from the current file
        List<Task> fileSnapshot = this.loadFromFile(true); // Load directly from file
        // Step 2: Take a snapshot of the current buffer (pre-flush state)
        int snapshotHashBeforeFlush = Objects.hash(super.storageList.toArray());
        // Step 3: Perform a validity check using a fresh snapshot from the file
        int expectedListHash = eventLogger.tryLogReplay(fileSnapshot);
        // Step 3: Compare the expected state with the current in-memory state
        if (expectedListHash == snapshotHashBeforeFlush) {
            System.out.println("Log replay is valid. Applying logs.");
            eventLogger.replayLog(filePath);  // Step 4: Apply logs if valid
        } else {
            System.err.println("Flush detected a drift! Falling back to full persistAll.");
            persistAll();  // Step 5: Full write to fix inconsistencies
            eventLogger.clearLog();
        }

        dirtySet.clear();
    }

    /**
     * Marks a task as modified, scheduling it for persistence.
     *
     * @param id The unique identifier of the task.
     * @return The same {@link UUID} of the marked task.
     */
    @Override
    public UUID markDirty(UUID id) {
        dirtySet.add(id);
        TaskEventObject.getInstance().dispatch(new TaskEvent(TaskEvent.EventType.UPDATE, storageMap.get(id)));
        return id;
    }

    /**
     * Persists all tasks to disk, overwriting the existing file.
     * Maintains JSON formatting and ensures atomic writes.
     */
    private void persistAll() {
        if (dirtySet.isEmpty()) return; // No changes, no need to persist

        try {
            backupCurrentFileIfExists(); // Backup before overwriting

            Path tempFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");

            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("[\n");

                int size = storageList.size();
                for (int i = 0; i < size; i++) {
                    writer.write(serializeTask(storageList.get(i)));

                    if (i < size - 1) {
                        writer.write(",\n"); // Ensure proper formatting
                    } else {
                        writer.write("\n"); // Last entry, no trailing comma
                    }
                }

                writer.write("]\n");
            }

            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            dirtySet.clear(); // Reset tracking

            System.out.println("Persisted all tasks to file.");

        } catch (IOException e) {
            System.err.println("Error persisting all tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads tasks from the file. Can return a list or load directly into memory.
     *
     * @param returnListOnly If true, returns a list of tasks instead of modifying internal storage.
     * @return List of tasks (only if returnListOnly is true), otherwise null.
     */
    private List<Task> loadFromFile(boolean returnListOnly) {
        List<Task> taskList = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return returnListOnly ? taskList : null;
        }

        try {
            Map<UUID, Task> taskMap = DataFileUtils.readTasksFromFile(filePath);
            taskList.addAll(taskMap.values());
        } catch (IOException e) {
            System.err.println("Error reading tasks from file: " + e.getMessage());
            if (!returnListOnly) {
                attemptBackupRecovery();
            }
        }

        if (!returnListOnly) {
            // Load into memory (only during initialization)
            super.storageList.clear();
            super.storageMap.clear();
            for (Task task : taskList) {
                super.storageList.add(task);
                super.storageMap.put(task.getId(), task);
            }
        }

        return returnListOnly ? taskList : null;
    }



    /**
     * Creates a backup of the current file before overwriting.
     *
     * @throws IOException If the backup operation fails.
     */
    private void backupCurrentFileIfExists() throws IOException {
        if (Files.exists(filePath)) {
            Path backupPath = Paths.get(filePath + ".bak");
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Attempts to recover data from a backup file if the main file is corrupted.
     */
    private void attemptBackupRecovery() {
        Path backupPath = Paths.get(filePath.toString() + ".bak");
        if (!Files.exists(backupPath)) {
            System.err.println("No backup file found.");
            return;
        }

        try {
            List<Task> recoveredTasks = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(backupPath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Task task = deserializeTask(line);
                    if (task != null) {
                        recoveredTasks.add(task);
                    }
                }
            }

            super.storageList.clear();
            super.storageMap.clear();

            for (Task task : recoveredTasks) {
                super.storageList.add(task);
                super.storageMap.put(task.getId(), task);
            }

            Files.copy(backupPath, filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Backup successfully restored.");

        } catch (IOException e) {
            System.err.println("Backup recovery failed.");
        }
    }
}
