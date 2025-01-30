package repository;

import entity.tasks.Task;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static util.TaskDeserializer.deserializeTask;
import static util.TaskSerializer.serializeTask;

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
    private final Set<UUID> dirtySet = new HashSet<>(); // Tracks modified tasks

    /**
     * Constructs a {@code FileBackedTaskRepository} and loads existing tasks from the specified file.
     *
     * @param filePath The file path where tasks will be persisted.
     */
    public FileBackedTaskRepository(Path filePath) {
        this.filePath = filePath;
        loadFromFile();
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
        dirtySet.add(result.getId()); // Mark as dirty
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
            dirtySet.add(task.getId()); // Mark as dirty (removal)
        }
        return task;
    }

    /**
     * Flushes all modified tasks to disk.
     * If no changes were made, this operation is skipped.
     */
    @Override
    public void flush() {
        if (dirtySet.isEmpty()) return; // No changes, skip flush

        System.out.println("Flushing modified tasks to file...");
        persistAll();
        dirtySet.clear(); // Reset tracking
    }

    /**
     * Marks a task as modified, scheduling it for persistence.
     *
     * @param id The unique identifier of the task.
     * @return The same {@link UUID} of the marked task.
     */
    @Override
    public UUID markDirty(UUID id) {
        dirtySet.add(id); // Mark the task as modified
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
     * Loads tasks from the file into memory.
     * Ensures JSON validity and recovers from backup if needed.
     */
    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return; // No file, start fresh
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            List<String> lines = reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());

            if (lines.isEmpty() || !lines.get(0).equals("[") || !lines.get(lines.size() - 1).equals("]")) {
                throw new IOException("Invalid file format");
            }

            for (int i = 1; i < lines.size() - 1; i++) {
                String line = lines.get(i).replaceAll(",$", ""); // Remove trailing commas
                Task task = deserializeTask(line);
                if (task != null) {
                    super.storageList.add(task); // Maintain order
                    super.storageMap.put(task.getId(), task); // Fast lookup
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            attemptBackupRecovery();
        }
    }

    /**
     * Creates a backup of the current file before overwriting.
     *
     * @throws IOException If the backup operation fails.
     */
    private void backupCurrentFileIfExists() throws IOException {
        if (Files.exists(filePath)) {
            Path backupPath = Paths.get(filePath.toString() + ".bak");
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
