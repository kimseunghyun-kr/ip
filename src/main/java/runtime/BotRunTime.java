package runtime;

import dispatcher.IDispatcher;
import entity.command.TerminationCommand;
import exceptions.UserFacingException;
import repository.entitymanager.TaskFlusher;


/**
 * Implements {@link IBotRunTime} to manage the chatbot runtime execution.
 * This class handles user input, resolves actions, and manages the chatbot lifecycle.
 */
public class BotRunTime implements IBotRunTime {

    /**
     * Manages task persistence and flushing operations.
     */
    private final TaskFlusher taskFlusher;

    private final IDispatcher dispatcher;

    /**
     * Constructs a {@code BotRunTime} with required dependencies.
     *
     * @param dispatcher  The controller responsible for receiving and resolving user actions.
     * @param taskFlusher The service responsible for managing task persistence.
     */
    public BotRunTime(TaskFlusher taskFlusher, IDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.taskFlusher = taskFlusher;
    }

    /**
     * Starts the chatbot runtime.
     * <p>
     * The bot continuously reads user input, resolves commands, and executes them.
     * If a {@link TerminationCommand} is encountered, the bot exits gracefully.
     * </p>
     * <p>
     * If a {@link UserFacingException} occurs, it is caught and logged at the outermost loop.
     * </p>
     */
    public void run() {
        taskFlusher.start();
        try {
            dispatcher.run(); // Delegate to CLI or GUI controller
        } catch (Exception e) {
            System.out.println("Unhandled exception in BotRunTime: " + e.getMessage());
        } finally {
            taskFlusher.stop();
        }
    }
}
