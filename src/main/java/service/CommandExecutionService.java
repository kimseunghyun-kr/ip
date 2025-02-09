package service;

import static entity.command.UpdateCommand.INTERACTIVEMODESTRING;

import controller.ControllerResponse;
import entity.command.TerminationCommand;
import entity.command.UpdateCommand;
import service.dao.CommandDao;
import service.interactiveexecutionservice.InteractiveExecutionService;

/**
 * Service for executing commands and managing interactive sessions.
 */
public class CommandExecutionService {
    /**
     * Signal indicating termination of the service.
     */
    public static final String TERMSIG = "TERMSIG";

    /**
     * Service handling interactive command execution.
     */
    private final InteractiveExecutionService interactiveExecutionService;

    /**
     * Handles action resolution and command processing.
     */
    private final ActionHandler actionHandler;

    /**
     * Constructs a CommandExecutionService instance.
     *
     * @param interactiveExecutionService The service managing interactive execution.
     * @param actionHandler The handler resolving actions and commands.
     */
    public CommandExecutionService(InteractiveExecutionService interactiveExecutionService,
                                   ActionHandler actionHandler) {
        this.interactiveExecutionService = interactiveExecutionService;
        this.actionHandler = actionHandler;
    }

    /**
     * Executes a command based on the input provided.
     *
     * @param input The user input command string.
     * @return A response message or a termination signal.
     */
    public String runCommand(String input) {
        if (interactiveExecutionService.isActiveSession()) {
            return interactiveExecutionService.handleInteractiveUpdate(input);
        }
        CommandDao command = actionHandler.resolveAction(input);
        ControllerResponse<?> response = command.execute();

        if (command.getCommand() instanceof UpdateCommand && response.getMessage().equals(INTERACTIVEMODESTRING)) {
            interactiveExecutionService.startInteractiveUpdate(command.getParams());
            return "Interactive update session started";
        }

        if (command.getCommand() instanceof TerminationCommand) {
            return TERMSIG;
        }

        return response.toString();
    }
}
