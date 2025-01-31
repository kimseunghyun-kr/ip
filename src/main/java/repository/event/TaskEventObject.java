package repository.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A singleton event bus for handling task events.
 */
public class TaskEventObject {
    private static final TaskEventObject INSTANCE = new TaskEventObject();
    private final List<Consumer<TaskEvent>> listeners = new CopyOnWriteArrayList<>();

    private TaskEventObject() {}  // Private constructor for Singleton

    public static TaskEventObject getInstance() {
        return INSTANCE;
    }

    public void register(Consumer<TaskEvent> listener) {
        listeners.add(listener);
    }

    public void dispatch(TaskEvent event) {
        for (Consumer<TaskEvent> listener : listeners) {
            listener.accept(event);
        }
    }
}
