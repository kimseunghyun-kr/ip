package util;

import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.ToDo;

/**
 * Utility class for converting {@link Task} objects into a string format for storage.
 */
public class TaskSerializer {

    /**
     * Converts a {@link Task} into a string representation.
     *
     * <p>Format: {@code UUID|TYPE|COMPLETED|NAME|[EXTRA_FIELDS]}</p>
     * <ul>
     *     <li>{@code UUID} - Task ID</li>
     *     <li>{@code TYPE} - "T" (ToDo), "D" (Deadline), "E" (Event)</li>
     *     <li>{@code COMPLETED} - "1" for done, "0" for not done</li>
     *     <li>{@code NAME} - Task description</li>
     *     <li>Extra fields:
     *         <ul>
     *             <li>Deadline: {@code dueBy}</li>
     *             <li>Event: {@code startAt | endBy}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param task The task to serialize.
     * @return The formatted string representing the task.
     */
    public static String serializeTask(Task task) {
        StringBuilder sb = new StringBuilder();

        sb.append(task.getId()).append("|");

        if (task instanceof DeadLine) {
            DeadLine dl = (DeadLine) task;
            sb.append("D|").append(dl.getCompleted() ? "1" : "0").append("|")
                    .append(dl.getName()).append("|").append(dl.getDueby());
        } else if (task instanceof Events) {
            Events ev = (Events) task;
            sb.append("E|").append(ev.getCompleted() ? "1" : "0").append("|")
                    .append(ev.getName()).append("|").append(ev.getStartat()).append("|").append(ev.getEndby());
        } else if (task instanceof ToDo) {
            sb.append("T|").append(task.getCompleted() ? "1" : "0").append("|")
                    .append(task.getName());
        } else {
            sb.append("UNKNOWN");
        }

        return sb.toString();
    }
}
