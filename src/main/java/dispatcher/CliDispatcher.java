package dispatcher;

import static util.ChatBotUtil.exitSequence;
import static util.ChatBotUtil.introSequence;
import static util.ChatBotUtil.linesep;

import java.util.Scanner;

import controller.ControllerResponse;
import entity.command.TerminationCommand;
import exceptions.UserFacingException;
import service.ActionHandler;
import service.CommandDao;

public class CliDispatcher implements IDispatcher {
    /** Scanner for reading user input. */
    private final Scanner scanner = new Scanner(System.in);
    /** Handles action resolution and command execution. */
    private final ActionHandler actionHandler;

    public CliDispatcher(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public void run() {
        linesep();
        introSequence();
        while (true) {
            try {
                linesep();
                String input = scanner.nextLine();
                linesep();
                CommandDao command = actionHandler.resolveAction(input);
                ControllerResponse response = command.execute();
                System.out.println(response);
                if (command.getCommand() instanceof TerminationCommand) {
                    break;
                }
            } catch (UserFacingException e) {
                System.out.println("outermost loop catch :: " + e.getMessage());
            }
        }
        exitSequence();
    }
}
