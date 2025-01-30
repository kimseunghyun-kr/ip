package dispatcher;

import repository.entityManager.TaskFlusher;

/**
 * Dispatches the GUI application by launching JavaFX.
 */
public class GuiDispatcher implements IDispatcher {
    /** Manages task persistence and flushing operations. */
    private final TaskFlusher taskFlusher;

    public GuiDispatcher(TaskFlusher taskFlusher) {
        this.taskFlusher = taskFlusher;
    }

    @Override
    public void run() {
        taskFlusher.start();
    }
}
