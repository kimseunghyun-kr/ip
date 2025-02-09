package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import entity.tasks.Task;

/**
 * Utility class for file-related operations such as reading lines and tasks.
 */
public class DataFileUtils {

    /**
     * Reads a file and returns a list of trimmed, non-empty lines.
     *
     * @param filePath The path to the file to be read.
     * @return A list of trimmed, non-empty lines.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static List<String> readNonEmptyLines(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return Collections.emptyList();
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Reads tasks from a file and returns a map of tasks identified by their UUID.
     *
     * @param filePath The path to the file containing task data.
     * @return A map where the key is the UUID and the value is the corresponding Task.
     * @throws IOException If an I/O error occurs or the file format is invalid.
     */
    public static Map<UUID, Task> readTasksFromFile(Path filePath) throws IOException {
        List<String> lines = readNonEmptyLines(filePath);
        Map<UUID, Task> taskMap = new LinkedHashMap<>();

        if (lines.isEmpty() || !lines.get(0).equals("[") || !lines.get(lines.size() - 1).equals("]")) {
            throw new IOException("Invalid file format");
        }

        for (int i = 1; i < lines.size() - 1; i++) {
            String line = lines.get(i).replaceAll(",$", "");
            Task task = TaskDeserializer.deserializeTask(line);
            if (task != null) {
                taskMap.put(task.getId(), task);
            }
        }

        return taskMap;
    }
}
