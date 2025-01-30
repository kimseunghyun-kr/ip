package mocks;

import entity.tasks.Task;
import exceptions.UserFacingException;
import repository.TaskRepository;
import service.TaskRepositoryCoordinatorService;

import java.util.UUID;

public class MockTaskCoordinatorRepositoryService extends TaskRepositoryCoordinatorService {
    MockTaskRepository mockTaskRepository;
    public MockTaskCoordinatorRepositoryService(MockTaskRepository mockTaskRepository) {
        super(mockTaskRepository, mockTaskRepository);
        this.mockTaskRepository = mockTaskRepository;;
    }
    public Task findByOrder(int orderIndex){
        return this.mockTaskRepository.findByOrder(orderIndex- 1).get();
    }


    void markDirty(UUID uuid){
        return;
    }
}
