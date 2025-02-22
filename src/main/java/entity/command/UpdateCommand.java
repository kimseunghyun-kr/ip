package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import entity.TaskType;
import entity.tasks.Task;
import exceptions.UserFacingException;
import service.dao.TaskUpdateDao;
import util.DateTimeUtils;

/**
 * Command to update an existing task.
 */
public class UpdateCommand implements Command {
    public static final String INTERACTIVEMODESTRING = "IINIT";
    private ITaskController taskController;
    /**
     * Sets the task controller.
     *
     * @param taskService the task controller instance
     */
    @Override
    public void setTaskController(ITaskController taskService) {
        this.taskController = taskService;
    }

    /**
     * Executes the update command.
     *
     * @param parameters list of parameters for updating the task
     * @return ControllerResponse containing the updated task
     * @throws UserFacingException if parameters are invalid or missing
     */
    @Override
    public ControllerResponse<Task> execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            throw new UserFacingException("Which task are you planning to update?");
        }

        int taskId = Integer.parseInt(parameters.get(0));
        Task existingTask = (Task) taskController.findByOrder(taskId).getData();

        if (parameters.size() == 1) {
            return new ControllerResponse<>(INTERACTIVEMODESTRING, existingTask);
        }

        TaskUpdateDao updateDao = parseParameters(parameters);

        if (isTaskTypeChanging(existingTask, updateDao)) {
            validateNewTaskData(updateDao);
        }
        return taskController.updateTask(taskId, updateDao);
    }

    /**
     * Parses the parameters for task update.
     *
     * @param parameters list of update parameters
     * @return TaskUpdateDao object containing parsed data
     * @throws UserFacingException if format is invalid
     */
    private TaskUpdateDao parseParameters(List<String> parameters) {
        parameters.remove(0);
        TaskUpdateDao.TaskUpdateDaoBuilder builder = TaskUpdateDao.builder();

        for (String param : parameters) {
            String[] keyValue = param.split("::", 2);
            if (keyValue.length != 2) {
                throw new UserFacingException("Invalid format: " + param);
            }

            switch (keyValue[0].toLowerCase()) {
            case "tasktype" -> builder.taskType(keyValue[1]);
            case "name" -> builder.name(keyValue[1]);
            case "start" -> builder.startDate(DateTimeUtils.parseDateOrDateTime(keyValue[1]));
            case "end" -> builder.endDate(DateTimeUtils.parseDateOrDateTime(keyValue[1]));
            case "due" -> builder.dueDate(DateTimeUtils.parseDateOrDateTime(keyValue[1]));
            default -> throw new UserFacingException("Unknown attribute: " + keyValue[0]);
            }
        }
        return builder.build();
    }

    /**
     * Checks if the task type is being changed.
     *
     * @param existingTask the existing task
     * @param updateDao the update data
     * @return true if task type is changing, false otherwise
     */
    private boolean isTaskTypeChanging(Task existingTask, TaskUpdateDao updateDao) {
        return updateDao.getTaskType() != null
                && !existingTask.getClass().getSimpleName().equalsIgnoreCase(updateDao.getTaskType());
    }

    /**
     * Validates the new task data if task type is changing.
     *
     * @param updateDao the update data
     * @throws UserFacingException if required fields are missing for the new task type
     */
    private void validateNewTaskData(TaskUpdateDao updateDao) {
        switch (TaskType.valueOf(updateDao.getTaskType().toUpperCase())) {
        case EVENT -> {
            if (updateDao.getName() == null || updateDao.getStartDate() == null || updateDao.getEndDate() == null) {
                throw new UserFacingException("Event requires name, start date, and end date");
            }
        }
        case DEADLINE -> {
            if (updateDao.getName() == null || updateDao.getDueDate() == null) {
                throw new UserFacingException("Deadline requires name and due date");
            }
        }
        case TODO-> {
            if (updateDao.getName() == null) {
                throw new UserFacingException("ToDo requires a name");
            }
        }
        default -> throw new UserFacingException("Unknown task type: " + updateDao.getTaskType());
        }
    }
}
