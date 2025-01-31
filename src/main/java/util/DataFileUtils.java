package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import entity.tasks.Task;

public class DataFileUtils {

    /**
     * Reads a file and returns a list of trimmed, non-empty lines.
     */
    public static List<String> readNonEmptyLines(Path filePath) throws IOException {
        if (!Files.exists(filePath)) return Collections.emptyList();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Reads tasks from a file and returns a Map of tasks by their UUID.
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
