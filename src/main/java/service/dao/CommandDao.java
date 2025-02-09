package service.dao;

import java.util.List;

import controller.ControllerResponse;
import entity.command.Command;
import lombok.Data;

@Data
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
}

