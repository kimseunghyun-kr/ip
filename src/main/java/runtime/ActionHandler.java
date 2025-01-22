package runtime;

import DIContainer.Proxiable;
import entity.Actions;
import entity.Command.Command;
import entity.Command.CommandFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionHandler implements Proxiable {
    private final CommandFactory commandFactory;

    public ActionHandler(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public Command resolveAction(String input) {
        String[] split = input.split(" ");
        String command = split[0];
        // Extract the parameters (from index 1 to end)
        List<String> parameters = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(split, 1, split.length)));
        Actions action;
        try {
            action = Actions.fromString(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            action = Actions.INVALID;
        }
        Command generatedCommand = commandFactory.createCommand(action);
        try {
            if(action == Actions.ADD) {
                parameters.addFirst(command);
            }
            generatedCommand.execute(parameters);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + "parameter involved is probably wrong");
        }
        return generatedCommand;

    }
}
