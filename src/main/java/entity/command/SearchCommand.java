package entity.command;

import java.time.LocalDateTime;
import java.util.List;

import entity.TaskType;
import exceptions.UserFacingException;
import service.ITaskService;
import util.DateTimeUtils;


/**
 * Represents the "Search " command in the task management system.
 * This command interacts with {@link ITaskService} to search a task
 * based on the provided parameters.
 */
public class SearchCommand implements Command {
    private ITaskService taskService;
    @Override
    public void setTaskService(ITaskService taskService) {
        this.taskService = taskService;
    }


    @Override
    public void execute(List<String> parameters) {
        if (parameters.isEmpty()) {
            throw new UserFacingException("Please enter a search term in the "
                    + "following <keyword> <val1> <val2> <val...>");
        }

        String keyword = parameters.get(0);
        if (keyword.equalsIgnoreCase("UUID")) {
            String val1 = parameters.get(1);
            String response = taskService.searchOrder(val1);
            System.out.println(response);
            return;
        } else if (keyword.equalsIgnoreCase("DATE")) {
            if (parameters.size() < 2) {
                throw new UserFacingException("Date search term requires at least two parameters \n"
                        + "in the format search date <event/deadline> <null/yyyy-mm-dd> <null/yyyy-mm-dd>");
            }
            TaskType val1 = TaskType.valueOf(parameters.get(1).toUpperCase());
            if (val1.equals(TaskType.EVENT) && parameters.size() == 4) {
                if (parameters.get(2).equalsIgnoreCase("nil")) {
                    LocalDateTime val3 = DateTimeUtils.parseDateOrDateTime(parameters.get(3));
                    System.out.println(taskService.searchByDate(TaskType.EVENT, null, val3));
                    return;
                }
                LocalDateTime val2 = DateTimeUtils.parseDateOrDateTime(parameters.get(2));
                LocalDateTime val3 = DateTimeUtils.parseDateOrDateTime(parameters.get(3));
                System.out.println(taskService.searchByDate(TaskType.EVENT, val2, val3));
                return;
            } else if (val1.equals(TaskType.EVENT) && parameters.size() == 3) {
                LocalDateTime val2 = DateTimeUtils.parseDateOrDateTime(parameters.get(2));
                System.out.println(taskService.searchByDate(TaskType.EVENT , val2, null));
                return;
            } else if (val1.equals(TaskType.DEADLINE) && parameters.size() == 4) {
                LocalDateTime val3 = DateTimeUtils.parseDateOrDateTime(parameters.get(3));
                System.out.println(taskService.searchByDate(TaskType.DEADLINE , val3, null));
                return;
            }
            throw new UserFacingException("Date search term requires at least two parameters \n"
                    + "in the format search date <event/deadline> <null/yyyy-mm-dd> <null/yyyy-mm-dd>");


        } else if (parameters.size() == 2) {
            String response = taskService.searchByKeyword(parameters.get(1));
            System.out.println(response);
        }

        throw new UserFacingException("INVALID SEARCH TERM");
    }
}
