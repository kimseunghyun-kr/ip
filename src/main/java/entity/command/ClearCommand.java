package entity.command;

<<<<<<< Updated upstream
public class ClearCommand {
=======
import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;

public class ClearCommand implements Command {
    /** The task service used to manage tasks. */
    private ITaskController taskController;

    /**
     * Sets the task service for this command.
     *
     * @param taskController The task service instance to be used.
     */
    @Override
    public void setTaskController(ITaskController taskController) {
        this.taskController = taskController;
    }
    @Override
    public ControllerResponse execute(List<String> parameters) {
        return taskController.deleteAll();
    }
>>>>>>> Stashed changes
}
