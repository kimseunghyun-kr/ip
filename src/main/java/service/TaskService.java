package service;

import DIContainer.Proxiable;
import entity.Task;
import repository.TaskRepository;

public class TaskService implements Proxiable {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
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

    public String addTask(String taskName){
        Task newTask = new Task(taskName);
        String response = taskRepository.store(newTask);
        String header = "Nice! I have added this task \n";
        return header + response + "\n";
    }
}
