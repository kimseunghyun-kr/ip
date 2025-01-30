package entity.command;

import java.util.HashMap;
import java.util.Map;

import dicontainer.Proxiable;
import entity.Actions;
import service.ITaskService;



/**
 * Factory class for creating instances of {@link Command} based on the provided {@link Actions}.
 * Commands are instantiated dynamically and injected with an {@link ITaskService} instance.
 */
public class CommandFactory implements Proxiable {
    /** Maps actions to their corresponding command classes. */
    private static final Map<Actions, Class<? extends Command>> commandMap = new HashMap<>();
    /** The task service instance to be injected into created commands. */
    private final ITaskService taskService;
    /**
     * Constructs a {@code CommandFactory} with a given task service.
     *
     * @param taskService The task service instance to be used by created commands.
     */
    public CommandFactory(ITaskService taskService) {
        this.taskService = taskService;
    }

    static {
        commandMap.put(Actions.TERMINATE, TerminationCommand.class);
        commandMap.put(Actions.ADD, AddCommand.class);
        commandMap.put(Actions.LIST, ListCommand.class);
        commandMap.put(Actions.MARK, MarkCommand.class);
        commandMap.put(Actions.UNMARK, UnmarkCommand.class);
        commandMap.put(Actions.DELETE, DeleteCommand.class);
        commandMap.put(Actions.SEARCH, SearchCommand.class);
        commandMap.put(Actions.INVALID, InvalidCommand.class);
    }
    /**
     * Creates a command instance corresponding to the specified action.
     * If the action is not recognized, an {@link InvalidCommand} is returned.
     *
     * @param action The action for which a command should be created.
     * @return A new instance of the corresponding {@link Command}.
     * @throws RuntimeException if an error occurs during command instantiation.
     */
    public Command createCommand(Actions action) {
        Class<? extends Command> commandClass = commandMap.getOrDefault(action, InvalidCommand.class);
        try {
            Command command = commandClass.getDeclaredConstructor().newInstance();
            command.setTaskService(taskService); // Inject TaskService
            return command;
        } catch (Exception e) {
            throw new RuntimeException("Error creating command for action: " + action, e);
        }
    }
}
