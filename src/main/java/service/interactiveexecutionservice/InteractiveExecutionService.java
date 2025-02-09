package service.interactiveexecutionservice;

import java.util.List;

/**
 * Interface for handling interactive updates (CLI and GUI versions).
 */
public interface InteractiveExecutionService {
    String startInteractiveUpdate(List<String> parameters);
    String handleInteractiveUpdate(String input);
    boolean isActiveSession();
}
