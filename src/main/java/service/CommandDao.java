package service;

import java.util.List;

import controller.ControllerResponse;
import entity.command.Command;

public class CommandDao {
    private final Command command;
    private final List<String> params;

    public CommandDao(Command generatedCommand, List<String> parameters) {
        this.command = generatedCommand;
        this.params = parameters;
    }

    public ControllerResponse execute() {
        return command.execute(params);
    }
    public Command getCommand() {
        return command;
    }
}

