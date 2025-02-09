package entity.command;

import java.util.List;

import controller.ControllerResponse;
import controller.ITaskController;
import entity.tasks.Task;
import exceptions.UserFacingException;
import service.dao.TaskUpdateDao;
import util.DateTimeUtils;

public class UpdateCommand implements Command {
    private ITaskController taskController;
    public static final String INTERACTIVEMODESTRING = "IINIT";
    @Override
    public void setTaskController(ITaskController taskService) {
        this.taskController = taskService;
    }

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

    private TaskUpdateDao parseParameters(List<String> parameters) {
        parameters.remove(0);
        TaskUpdateDao.TaskUpdateDaoBuilder builder = TaskUpdateDao.builder();

        for (String param : parameters) {
            String[] keyValue = param.split("::", 2);
            if (keyValue.length != 2) throw new UserFacingException("Invalid format: " + param);

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

    private boolean isTaskTypeChanging(Task existingTask, TaskUpdateDao updateDao) {
        return updateDao.getTaskType() != null
                && !existingTask.getClass().getSimpleName().equalsIgnoreCase(updateDao.getTaskType());
    }
    private void validateNewTaskData(TaskUpdateDao updateDao) {
        switch (updateDao.getTaskType()) {
        case "Event" -> {
            if (updateDao.getName() == null || updateDao.getStartDate() == null || updateDao.getEndDate() == null) {
                throw new UserFacingException("Event requires name, start date, and end date");
            }
        }
        case "Deadline" -> {
            if (updateDao.getName() == null || updateDao.getDueDate() == null) {
                throw new UserFacingException("Deadline requires name and due date");
            }
        }
        case "ToDo" -> {
            if (updateDao.getName() == null) {
                throw new UserFacingException("ToDo requires a name");
            }
        }
        default -> throw new UserFacingException("Unknown task type: " + updateDao.getTaskType());
        }
    }





}
