package service.interactiveexecutionservice;

import java.util.List;
import java.util.Scanner;

/**
 * CLI-based interactive execution service.
 */
public class CliInteractiveExecutionService implements InteractiveExecutionService {
    private final Scanner scanner = new Scanner(System.in);
    private int taskId;
    private boolean activeSession = false;

    @Override
    public String startInteractiveUpdate(List<String> parameters) {
        if (parameters.isEmpty()) {
            return "Error: No task ID provided for interactive update.";
        }

        try {
            this.taskId = Integer.parseInt(parameters.get(0));
            activeSession = true;
            return "Interactive update started for Task " + taskId + ". Enter properties in `property:value` format.";
        } catch (NumberFormatException e) {
            return "Error: Invalid task ID.";
        }
    }

    @Override
    public String handleInteractiveUpdate(String input) {
        if (!activeSession) {
            return "Error: No active interactive update session.";
        }

        if ("done".equalsIgnoreCase(input)) {
            activeSession = false;
            return "Interactive update for Task " + taskId + " completed.";
        }

        // Process property:value updates
        if (input.contains(":")) {
            String[] parts = input.split(":", 2);
            String property = parts[0].trim();
            String value = parts[1].trim();

            // Apply update logic here
            System.out.println("Updated Task " + taskId + ": " + property + " -> " + value);
            return "Updated " + property + " to " + value;
        } else {
            return "Invalid format. Use property:value or type 'done' to finish.";
        }
    }

    @Override
    public boolean isActiveSession() {
        return this.activeSession;
    }
}

