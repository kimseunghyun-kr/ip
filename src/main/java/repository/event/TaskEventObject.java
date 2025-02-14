package repository.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import lombok.NoArgsConstructor;

/**
 * A singleton event bus for handling task events.
 */
@NoArgsConstructor
public class TaskEventObject {
    private static final TaskEventObject INSTANCE = new TaskEventObject();
    private final List<Consumer<TaskEvent>> listeners = new CopyOnWriteArrayList<>();

    public static TaskEventObject getInstance() {
        return INSTANCE;
    }

    public void register(Consumer<TaskEvent> listener) {
        listeners.add(listener);
    }

    /**
     * Dispatches a task event to all registered listeners. (only one exists now)
     *
     * @param event The {@link TaskEvent} to be propagated.
     */
    public void dispatch(TaskEvent event) {
        for (Consumer<TaskEvent> listener : listeners) {
            listener.accept(event);
        }
    }
}
