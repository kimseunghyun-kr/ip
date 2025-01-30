package util;

import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.ToDo;

public class TaskSerializer {
    public static String serializeTask(Task task) {
        StringBuilder sb = new StringBuilder();

        // Include UUID as first field in all cases
        sb.append(task.getId()).append("|");

        if (task instanceof DeadLine) {
            DeadLine dl = (DeadLine) task;
            sb.append("D|").append(dl.getCompleted() ? "1" : "0").append("|")
                    .append(dl.getName()).append("|")
                    .append(dl.getDueby());
        } else if (task instanceof Events) {
            Events ev = (Events) task;
            sb.append("E|").append(ev.getCompleted() ? "1" : "0").append("|")
                    .append(ev.getName()).append("|")
                    .append(ev.getStartat()).append("|").append(ev.getEndby());
        } else if (task instanceof ToDo) {
            sb.append("T|").append(task.getCompleted() ? "1" : "0").append("|")
                    .append(task.getName());
        } else {
            sb.append("UNKNOWN");
        }

        return sb.toString();
    }
}
