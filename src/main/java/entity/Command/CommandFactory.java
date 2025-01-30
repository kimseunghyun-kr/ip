package entity.Command;

import DIContainer.Proxiable;
import entity.Actions;
import service.ITaskService;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory implements Proxiable {
    private final ITaskService taskService;
    private static final Map<Actions, Class<? extends Command>> commandMap = new HashMap<>();

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
