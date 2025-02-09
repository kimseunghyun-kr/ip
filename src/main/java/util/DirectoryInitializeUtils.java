package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for initializing directories and file paths required by the application.
 * <p>
 * This class provides methods to create necessary directories and return file paths
 * for storing task data and logs.
 * </p>
 */
public class DirectoryInitializeUtils {

    /**
     * Initializes the data directory and returns the file path for task storage.
     * <p>
     * Ensures that the directory exists before returning the path to the tasks file.
     * </p>
     *
     * @return The {@link Path} to the tasks file.
     * @throws RuntimeException If the directory creation fails.
     */
    public static Path initializeDataDirectory() {
        // Define the file path relative to the project root
        Path filePath = Paths.get("./appData/tasks.txt");

        // Ensure the directory exists
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + filePath.getParent(), e);
        }
        return filePath;
    }

    /**
     * Initializes the log directory and returns the file path for logging.
     * <p>
     * Ensures that the directory exists before returning the path to the log file.
     * </p>
     *
     * @return The {@link Path} to the log file.
     * @throws RuntimeException If the directory creation fails.
     */
    public static Path initializeLogDirectory() {
        // Define the file path relative to the project root
        Path filePath = Paths.get("./appData/roll-log.txt");

        // Ensure the directory exists
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + filePath.getParent(), e);
        }
        return filePath;
    }
}
