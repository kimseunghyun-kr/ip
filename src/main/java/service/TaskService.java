package service;

import entity.TaskType;
import entity.tasks.Task;
import entity.tasks.TaskFactory;
import exceptions.UserFacingException;
import repository.ITaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskService implements ITaskService {
    private final TaskRepositoryCoordinatorService taskRepositoryCoordinatorService;
    private final ITaskRepository taskRepository;

    public TaskService(TaskRepositoryCoordinatorService taskRepositoryCoordinatorService, ITaskRepository taskRepository) {
        this.taskRepositoryCoordinatorService = taskRepositoryCoordinatorService;
        this.taskRepository = taskRepository;
    }

    public String markDone(int index) {
        Task selectedTask = taskRepositoryCoordinatorService.findByOrder(index);
        if (selectedTask.getCompleted()) {
            return "the task is already marked as done \n" + selectedTask + "  ->  " + selectedTask;
        } else {
            String response = "the task has been marked as done \n" + selectedTask;
            selectedTask.toggleCompleted();
            taskRepositoryCoordinatorService.markDirty(selectedTask.getId());
            response = response + "  ->  " + selectedTask;
            return response;
        }
    }

    public String markUndone(int index) {
        Task selectedTask = taskRepositoryCoordinatorService.findByOrder(index - 1);
        if (selectedTask.getCompleted()) {
            String response = "the task has been marked as undone \n" + selectedTask;
            selectedTask.toggleCompleted();
            taskRepositoryCoordinatorService.markDirty(selectedTask.getId());
            response = response + "  ->  " + selectedTask;
            return response;
        } else {
            return "the task is already marked as undone \n" + selectedTask + "  ->  " + selectedTask;
        }
    }

    public String getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        int counter = 1;
        StringBuilder result = new StringBuilder();
        for(Task word : tasks) {
            result.append(counter).append(".").append(word).append("\n");
            counter++;
        }
        return result.toString();
    }

    public String addTask(List<String> taskParams) {
        TaskType taskType = TaskType.valueOf(taskParams.get(0).toUpperCase());
        taskParams.remove(0);
        Task newTask = TaskFactory.createTask(taskType, taskParams);
        Task response = taskRepository.save(newTask);

        String header = "Nice! I have added this task \n added: ";
        return header + response + "\n";
    }

    @Override
    public String deleteTask(int taskId) {
        Task deleted = taskRepository.deleteById(taskId-1);
        Integer remainingTasks = taskRepository.remainingTasks();
        StringBuilder result = new StringBuilder();
        result.append("the following task has been deleted\n")
                .append(deleted)
                .append("\n you have ")
                .append(remainingTasks)
                .append(" tasks in the list");

        return result.toString();
    }

    public String SearchOrder(String uuidstr) {
        try{
            UUID uuid = UUID.fromString(uuidstr);
            return Integer.valueOf(taskRepository.findOrder(uuid)).toString();
        } catch (IllegalArgumentException e) {
            throw new UserFacingException("the uid input is not a valid UUID");
        }
    }

    @Override
    public String SearchByDate(TaskType type, LocalDateTime from, LocalDateTime to) {
        List<Task> withinDates = taskRepository.findAllFromWhenToWhen(type, from, to);
        StringBuilder sb = new StringBuilder();
        sb.append("The following tasks have been searched\n");
        for(Task withinDate : withinDates) {
            sb.append(withinDate).append("\n");
        }
        return sb.toString();
    }
}
