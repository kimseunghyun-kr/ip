package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryInitializeUtils {
    public static Path initializeDirectory() {
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
}
