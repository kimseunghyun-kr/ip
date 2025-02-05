package repository.entitymanager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import repository.IFileBackedTaskRepository;


/**
 * Periodically calls {@code flush()} on the {@link IFileBackedTaskRepository}.
 * This helps in reducing I/O operations by batching writes over time.
 */
public class TaskFlusher {

    private final IFileBackedTaskRepository taskRepository;
    private final ScheduledExecutorService scheduler;
    private final Runnable flushTask;
    private final LocalDateTime startTime;

    /**
     * Constructs a {@code TaskFlusher} with the specified task repository.
     * Initializes a scheduled executor to manage periodic flushing.
     *
     * @param taskRepository The repository that manages file-backed task persistence.
     */
    public TaskFlusher(IFileBackedTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.startTime = LocalDateTime.now();
        this.flushTask = this.createFlushTask();
    }

    /**
     * Creates a runnable task that logs the elapsed runtime and calls {@code flush()} on the repository.
     *
     * @return A {@link Runnable} task for periodic flushing.
     */
    private Runnable createFlushTask() {
        return () -> {
            try {
                long elapsedSeconds = Duration.between(startTime, LocalDateTime.now()).getSeconds();
                System.out.printf("Task autosaved, program active for %d seconds\n", elapsedSeconds);
                taskRepository.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    /**
     * Starts the periodic flushing process.
     * The repository is flushed every 10 seconds to persist changes.
     */
    public void start() {
        scheduler.scheduleAtFixedRate(flushTask, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * Stops the periodic flushing process and ensures all pending changes are persisted.
     * Calls {@code flush()} one last time before shutting down the scheduler.
     */
    public void stop() {
        taskRepository.flush();
        scheduler.shutdown();
    }
}

