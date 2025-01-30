package runtime;

import entity.Command.Command;
import entity.Command.TerminationCommand;
import exceptions.UserFacingException;
import repository.entityManager.TaskFlusher;

import java.util.Scanner;

import static util.ChatBotUtil.*;

/**
 * Implements {@link IBotRunTime} to manage the chatbot runtime execution.
 * This class handles user input, resolves actions, and manages the chatbot lifecycle.
 */
public class BotRunTime implements IBotRunTime {

    /** Handles action resolution and command execution. */
    private final ActionHandler actionHandler;

    /** Manages task persistence and flushing operations. */
    private final TaskFlusher taskFlusher;

    /** Scanner for reading user input. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructs a {@code BotRunTime} with required dependencies.
     *
     * @param actionHandler The handler responsible for resolving user actions.
     * @param taskFlusher The service responsible for managing task persistence.
     */
    public BotRunTime(ActionHandler actionHandler, TaskFlusher taskFlusher) {
        this.actionHandler = actionHandler;
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
    public void run(){
        taskFlusher.start();
        linesep();
        introSequence();
        while(true) {
            try {
                linesep();
                String input = scanner.nextLine();
                linesep();
                Command command = actionHandler.resolveAction(input);
                if (command instanceof TerminationCommand) {
                    break;
                }
            } catch(UserFacingException e) {
                System.out.println("outermost loop catch :: " + e.getMessage());
            }
        }
        taskFlusher.stop();
        exitSequence();
    }
}
