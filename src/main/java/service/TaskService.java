package service;

import entity.TaskType;
import entity.tasks.Task;
import entity.tasks.TaskFactory;
import exceptions.UserFacingException;
import repository.IFileBackedTaskRepository;
import repository.ITaskRepository;

import java.util.List;

public class TaskService implements ITaskService {
    private final ITaskRepository taskRepository;
    private final IFileBackedTaskRepository taskBuffer;

    public TaskService(ITaskRepository taskRepository, IFileBackedTaskRepository taskBuffer) {
        this.taskRepository = taskRepository;
        this.taskBuffer = taskBuffer;
    }

    public String markDone(int index) {
        Task selectedTask = taskRepository.findById(index - 1).orElseThrow(()->new UserFacingException("Task not found"));
        if(selectedTask == null) {
            throw new UserFacingException("Task not found");
        }
        if (selectedTask.getCompleted()) {
            return "the task is already marked as done \n" + selectedTask + "  ->  " + selectedTask;
        } else {
            String response = "the task has been marked as done \n" + selectedTask;
            selectedTask.toggleCompleted();
            taskBuffer.markDirty(selectedTask.getId());
            response = response + "  ->  " + selectedTask;
            return response;
        }
    }

    public String markUndone(int index) {

        Task selectedTask = taskRepository.findById(index - 1).orElseThrow(()->new UserFacingException("Task not found"));
        if (selectedTask.getCompleted()) {
            String response = "the task has been marked as undone \n" + selectedTask;
            selectedTask.toggleCompleted();
            taskBuffer.markDirty(selectedTask.getId());
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
}
