package repository.entityManager;
import entity.tasks.Task;
import repository.ITaskRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A toy "EntityManager" that simulates
 * transaction boundaries around a repository.
 */
public class BabyEntityManager {

    private final ITaskRepository repository;
    private boolean inTransaction = false;
    private final List<Task> newEntities = new ArrayList<>();
    private final List<Integer> removedEntities = new ArrayList<>();

    public BabyEntityManager(ITaskRepository repository) {
        this.repository = repository;
    }

    public void beginTransaction() {
        if (inTransaction) {
            throw new IllegalStateException("Transaction already started!");
        }
        inTransaction = true;
    }

    public void persist(Task entity) {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction active!");
        }
        newEntities.add(entity);
    }

    public void remove(Integer taskId) {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction active!");
        }
        removedEntities.add(taskId);
    }

    public void commit() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction active!");
        }
        // write changes to repository
        for (Task t : newEntities) {
            repository.save(t);
        }
        for (Integer id : removedEntities) {
            repository.deleteByOrder(id);
        }
        // clear staging
        newEntities.clear();
        removedEntities.clear();
        inTransaction = false;
    }

    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction active!");
        }
        newEntities.clear();
        removedEntities.clear();
        inTransaction = false;
    }
}
