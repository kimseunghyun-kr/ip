package runtime;

import DIContainer.Proxiable;
import entity.Actions;
import entity.Command.AddCommand;
import entity.Command.Command;
import entity.Command.CommandFactory;
import exceptions.UserFacingException;
import util.CommandMapper;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    public CommandDAO resolveAction(String input) throws UserFacingException {
        String[] split = input.trim().split(" ");
        String command = split[0];

        // Use static CommandMapper
        Actions action = CommandMapper.mapCommandToAction(command);

        List<String> parameters = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(split, 1, split.length)));

        Command generatedCommand = commandFactory.createCommand(action);
        if (generatedCommand instanceof AddCommand) {
            parameters.add(0,command);
        }
        return new CommandDAO(generatedCommand, parameters);
    }
}