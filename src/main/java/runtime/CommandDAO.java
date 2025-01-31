package runtime;

import java.util.List;

import entity.Command.Command;

public class CommandDAO {
    Command command;
    List<String> params;

    public CommandDAO(Command generatedCommand, List<String> parameters) {
        this.command = generatedCommand;
        this.params = parameters;
    }

    public void execute() {
        command.execute(params);
    }
}
