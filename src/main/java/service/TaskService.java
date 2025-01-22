package service;

import DIContainer.Proxiable;
import entity.TaskType;
import entity.tasks.Task;
import entity.tasks.TaskFactory;
import repository.ITaskRepository;

import java.util.List;

public class TaskService implements Proxiable {
    private final ITaskRepository taskRepository;

    public TaskService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public String markDone(int index){
        try {
            Task selectedTask = taskRepository.getById(index-1);
            if(selectedTask.getCompleted()) {
                return "the task is already marked as done \n" + selectedTask + "  ->  " + selectedTask;
            } else {
                String response = "the task has been marked as done \n" + selectedTask;
                selectedTask.toggleCompleted();
                response = response + "  ->  " + selectedTask;
                return response;
            }
        } catch (IndexOutOfBoundsException e) {
            return "the requested index" + index + "is out of bounds";
        }
    }

    public String markUndone(int index){
        try {
            Task selectedTask = taskRepository.getById(index-1);
            if(selectedTask.getCompleted()) {
                String response = "the task has been marked as undone \n" + selectedTask;
                selectedTask.toggleCompleted();
                response = response + "  ->  " + selectedTask;
                return response;
            } else {
                return "the task is already marked as undone \n" + selectedTask + "  ->  " + selectedTask;
            }
        } catch (IndexOutOfBoundsException e) {
            return "the requested index" + index + "is out of bounds";
        }
    }

    public String getAllTasks(){
        return taskRepository.getAll();
    }

    public String addTask(List<String> taskParams){
        TaskType taskType = TaskType.valueOf(taskParams.getFirst().toUpperCase());
        taskParams.removeFirst();
        Task newTask = TaskFactory.createTask(taskType, taskParams);
        String response = taskRepository.store(newTask);
        String header = "Nice! I have added this task \n";
        return header + response + "\n";
    }
}
