package service.dao;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object (DTO) for updating task details.
 * <p>
 * This class holds the updated attributes of a task, which can include its name,
 * task type, start date, end date, or due date. Fields that are {@code null}
 * indicate no change to that attribute.
 * </p>
 */
@Data
@Builder
public class TaskUpdateDao {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime dueDate;
    private String taskType;
}

