package dispatcher;

import static util.ChatBotUtil.exitSequence;
import static util.ChatBotUtil.introSequence;
import static util.ChatBotUtil.linesep;

import java.util.Scanner;

import exceptions.UserFacingException;
import service.CommandExecutionService;

public class CliDispatcher implements IDispatcher {
    /**
     * Scanner for reading user input.
     */
    private final Scanner scanner = new Scanner(System.in);
    /**
     * Handles action resolution and command execution.
     */
    private final CommandExecutionService commandExecutionService;


    public CliDispatcher(CommandExecutionService commandExecutionService) {
        this.commandExecutionService = commandExecutionService;
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
                String response = commandExecutionService.runCommand(input);
                if (response.equals(CommandExecutionService.TERMSIG)) {
                    break;
                }
                System.out.println(response);
            } catch (UserFacingException e) {
                System.out.println("outermost loop catch :: " + e.getMessage());
            }
        }
        exitSequence();
    }
}
