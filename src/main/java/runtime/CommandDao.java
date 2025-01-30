package runtime;

import java.util.List;

import entity.command.Command;

public class CommandDao {
    private final Command command;
    private final List<String> params;

    public CommandDao(Command generatedCommand, List<String> parameters) {
        this.command = generatedCommand;
        this.params = parameters;
    }

    public void execute() {
        command.execute(params);
    }
    public Command getCommand() {
        return command;
    }
}

