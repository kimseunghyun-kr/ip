package repository.entityManager;

import repository.IFileBackedTaskRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodically calls flush() on the FileBackedTaskRepository.
 * This reduces I/O by batching changes over time.
 */
public class TaskFlusher {

    private final IFileBackedTaskRepository taskRepository;
    private final ScheduledExecutorService scheduler;
    private final LocalDateTime startTime;

    public TaskFlusher(IFileBackedTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.startTime = LocalDateTime.now();
    }

    public void start() {
        // Flush every 10 seconds, for example
        scheduler.scheduleAtFixedRate(() -> {
            try {
                long elapsedSeconds = Duration.between(startTime, LocalDateTime.now()).getSeconds();
                System.out.printf("task autosaved, program active for %d seconds\n", elapsedSeconds);
                taskRepository.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        taskRepository.flush();
        scheduler.shutdown();
    }
}

