package runtime;

import DIContainer.Proxiable;
import entity.Actions;
import entity.Command.Command;
import entity.Command.CommandFactory;
import exceptions.UserFacingException;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Handles user commands and resolves them into appropriate actions.
 * Uses a {@link CommandFactory} to create command instances and execute them with parameters.
 */
public class ActionHandler implements Proxiable {
    private final CommandFactory commandFactory;

    /**
     * Constructs an {@code ActionHandler} with the specified command factory.
     *
     * @param commandFactory The factory responsible for creating command instances.
     */
    public ActionHandler(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Resolves a user input string into a command and executes it.
     *
     * @param input The user-provided command string.
     * @return The executed {@link Command} instance.
     * @throws UserFacingException If the command is invalid or deprecated.
     */
    public Command resolveAction(String input) {
        String[] split = input.split(" ");
        String command = split[0];

        List<String> parameters = Arrays.asList(Arrays.copyOfRange(split, 1, split.length));
        Actions action = resolveActionType(command);
        Command generatedCommand = commandFactory.createCommand(action);

        try {
            if (action == Actions.ADD) {
                parameters.add(0, command);
            }
            generatedCommand.execute(parameters);
        } catch (UserFacingException e) {
            System.out.println("User error: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Datetime parsing error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Execution error: " + e.getMessage());
        }

        return generatedCommand;
    }

    /**
     * Determines the appropriate {@link Actions} type based on a command string.
     *
     * @param command The command string provided by the user.
     * @return The corresponding {@link Actions} enum value.
     * @throws UserFacingException If the command is explicitly deprecated.
     */
    private Actions resolveActionType(String command) {
        if (command.equalsIgnoreCase("add")) {
            throw new UserFacingException("add is a deprecated action, please specify type of action");
        }
        if (command.equalsIgnoreCase("find")) {
            return Actions.SEARCH;
        }
        try {
            return Actions.fromString(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Actions.INVALID;
        }
    }
}