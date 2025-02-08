package mocks;

import java.util.UUID;

import entity.tasks.Task;
import service.TaskRepositoryCoordinatorService;

public class MockTaskCoordinatorRepositoryService extends TaskRepositoryCoordinatorService {
    MockTaskRepository mockTaskRepository;

    public MockTaskCoordinatorRepositoryService(MockTaskRepository mockTaskRepository) {
        super(mockTaskRepository, mockTaskRepository);
        this.mockTaskRepository = mockTaskRepository;
    }

    public Task findByOrder(int orderIndex) {
        return this.mockTaskRepository.findByOrder(orderIndex - 1).get();
    }


    void markDirty(UUID uuid) {
    }
}
