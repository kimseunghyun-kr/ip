package service;

import static entity.command.UpdateCommand.INTERACTIVEMODESTRING;

import controller.ControllerResponse;
import entity.command.TerminationCommand;
import entity.command.UpdateCommand;
import service.dao.CommandDao;
import service.interactiveexecutionservice.InteractiveExecutionService;


public class CommandExecutionService {
    public static final String TERMSIG = "TERMSIG";
    private final InteractiveExecutionService interactiveExecutionService;
    private final ActionHandler actionHandler;


    public CommandExecutionService(InteractiveExecutionService interactiveExecutionService,
                                   ActionHandler actionHandler) {
        this.interactiveExecutionService = interactiveExecutionService;
        this.actionHandler = actionHandler;
    }

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
