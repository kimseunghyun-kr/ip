package service.dao;

import java.util.List;

import controller.ControllerResponse;
import entity.command.Command;
import lombok.Data;

/**
 * Data Access Object for executing commands with parameters.
 */
@Data
public class CommandDao {
    /**
     * The command to be executed.
     */
    private final Command command;

    /**
     * The list of parameters associated with the command.
     */
    private final List<String> params;

    /**
     * Constructs a CommandDao instance.
     *
     * @param generatedCommand The command to be executed.
     * @param parameters The list of parameters for the command.
     */
    public CommandDao(Command generatedCommand, List<String> parameters) {
        this.command = generatedCommand;
        this.params = parameters;
    }

    /**
     * Executes the stored command with the provided parameters.
     *
     * @return ControllerResponse containing the result of the command execution.
     */
    public ControllerResponse<?> execute() {
        return command.execute(params);
    }
}
