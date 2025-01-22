package repository;

import entity.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskRepository implements ITaskRepository {
    public List<Task> storageList = new ArrayList<>();

    public String store(Task input)  {
        storageList.add(input);
        return "added: " + input;
    }

    public String getAll() {
        int counter = 1;
        StringBuilder result = new StringBuilder();
        for(Task word : storageList) {
            result.append(counter).append(".").append(word).append("\n");
            counter++;
        }
        return result.toString();
    }

    public Task getById(int index) {
        return storageList.get(index);
    }
}
