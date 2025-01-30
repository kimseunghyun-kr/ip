package repository;

import entity.tasks.Task;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static util.TaskDeserializer.deserializeTask;
import static util.TaskSerializer.serializeTask;

public class FileBackedTaskRepository extends TaskRepository implements IFileBackedTaskRepository {
    private final Path filePath;
    private final Set<UUID> dirtySet = new HashSet<>(); // Tracks modified tasks

    public FileBackedTaskRepository(Path filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    @Override
    public Task save(Task entity) {
        Task result = super.save(entity);
        dirtySet.add(result.getId()); // Mark as dirty
        return result;
    }

    @Override
    public Task deleteById(Integer index) {
        Task task = super.deleteById(index);
        if (task != null) {
            dirtySet.add(task.getId()); // Mark as dirty (removal)
        }
        return task;
    }

    @Override
    public void flush() {
        if (dirtySet.isEmpty()) return; // No changes, skip flush

        System.out.println("Flushing modified tasks to file...");
        persistAll();
        dirtySet.clear(); // Reset after flush
    }

    @Override
    public UUID markDirty(UUID id) {
        dirtySet.add(id); // Mark the task as modified
        return id;
    }
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


    private void persist() {
        try {
            backupCurrentFileIfExists();

            Path tempFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");

            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("[\n");

                List<Task> tasks = new ArrayList<>(super.storageList); // All tasks in order
                int size = tasks.size();

                for (int i = 0; i < size; i++) {
                    Task task = tasks.get(i);

                    // Ensure we write ALL tasks, but updated ones come from dirtySet
                    if (dirtySet.contains(task.getId()) || !Files.exists(filePath)) {
                        writer.write(serializeTask(task)); // Updated or new task
                    } else {
                        writer.write(serializeTask(task)); // Preserve old tasks
                    }

                    if (i < size - 1) {
                        writer.write(",\n"); // Maintain JSON-like formatting
                    } else {
                        writer.write("\n"); // Final entry, no trailing comma
                    }
                }

                writer.write("]\n");
            }

            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            dirtySet.clear(); // Reset dirty tracking after successful write

        } catch (IOException e) {
            System.err.println("Error persisting tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }


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

    private void backupCurrentFileIfExists() throws IOException {
        if (Files.exists(filePath)) {
            Path backupPath = Paths.get(filePath.toString() + ".bak");
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

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
