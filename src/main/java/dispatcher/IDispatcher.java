package dispatcher;

/**
 * Interface for dispatching and managing application execution flow.
 * <p>
 * Implementations of this interface handle the execution of tasks
 * or commands based on the application's runtime mode.
 * </p>
 */
public interface IDispatcher {

    /**
     * Starts the dispatcher and manages execution flow.
     */
    void run();
}
