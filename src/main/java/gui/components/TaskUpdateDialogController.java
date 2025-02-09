package gui.components;

import java.time.LocalDateTime;
import java.util.Objects;

import controller.ITaskController;
import entity.TaskType;
import entity.tasks.DeadLine;
import entity.tasks.Events;
import entity.tasks.Task;
import entity.tasks.ToDo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import service.dao.TaskUpdateDao;


/**
 * Controller for handling task updates via a dialog interface.
 * <p>
 * This class manages the user interface elements for modifying task details
 * such as name, type, and relevant dates. It ensures that only valid
 * modifications are applied before updating the task in the system.
 * </p>
 */
public class TaskUpdateDialogController {
    @Setter
    private static ITaskController taskController;

    @FXML private ComboBox<String> taskTypeComboBox;
    @FXML private TextField nameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private Button saveButton;

    private int taskId;
    private Task originalTask;

    /**
     * Initializes the task update dialog.
     * Populates the task type combo box and sets up event listeners.
     */
    public void initialize() {
        taskTypeComboBox.setItems(FXCollections.observableArrayList("Event", "Deadline", "ToDo"));
        taskTypeComboBox.setOnAction(event -> updateVisibleFields());
    }

    /**
     * Sets the task ID and loads the corresponding task data for editing.
     *
     * @param taskId The ID of the task to be updated.
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
        this.originalTask = (Task) taskController.findByOrder(taskId).getData();

        if (originalTask == null) {
            return;
        }

        nameField.setText(originalTask.getName());
        taskTypeComboBox.setValue(getTaskType(originalTask).name());
        populateFields(originalTask);
    }

    /**
     * Populates the date fields based on the task type.
     *
     * @param task The task whose details are to be populated.
     */
    private void populateFields(Task task) {
        resetFields();

        if (task instanceof Events event) {
            startDatePicker.setValue(event.getStartat().toLocalDate());
            endDatePicker.setValue(event.getEndby().toLocalDate());
        } else if (task instanceof DeadLine deadline) {
            dueDatePicker.setValue(deadline.getDueby().toLocalDate());
        }

        updateVisibleFields();
    }

    /**
     * Updates the visibility of date fields based on the selected task type.
     */
    private void updateVisibleFields() {
        String selectedType = taskTypeComboBox.getValue();
        resetFields();

        if ("Event".equals(selectedType)) {
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(true);
        } else if ("Deadline".equals(selectedType)) {
            dueDatePicker.setVisible(true);
        }
    }

    /**
     * Resets all date fields to be invisible and clears their values.
     */
    private void resetFields() {
        startDatePicker.setVisible(false);
        endDatePicker.setVisible(false);
        dueDatePicker.setVisible(false);

        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        dueDatePicker.setValue(null);
    }

    /**
     * Handles the save button click event by updating the task with modified details.
     */
    @FXML
    private void handleSave() {
        if (originalTask == null) {
            return;
        }

        TaskUpdateDao updateDao = buildTaskUpdateDao();
        taskController.updateTask(taskId, updateDao);

        closeDialog();
    }

    /**
     * Constructs a {@link TaskUpdateDao} object with updated task details.
     *
     * @return The built {@code TaskUpdateDao} containing updated task information.
     */
    private TaskUpdateDao buildTaskUpdateDao() {
        return TaskUpdateDao.builder()
                .taskType(getUpdatedTaskType())
                .name(getUpdatedName())
                .startDate(getUpdatedStartDateTime())
                .endDate(getUpdatedEndDateTime())
                .dueDate(getUpdatedDueDateTime())
                .build();
    }

    /**
     * Determines if the task type has been modified.
     *
     * @return The updated task type, or {@code null} if unchanged.
     */
    private String getUpdatedTaskType() {
        String selectedType = taskTypeComboBox.getValue();
        String currentType = getTaskType(originalTask).name();
        return !Objects.equals(selectedType, currentType) ? selectedType : currentType;
    }

    /**
     * Determines if the task name has been modified.
     *
     * @return The updated task name, or {@code null} if unchanged.
     */
    private String getUpdatedName() {
        return !Objects.equals(nameField.getText(), originalTask.getName())
                ? nameField.getText() : originalTask.getName();
    }

    /**
     * Gets the updated start date-time if modified.
     *
     * @return The new start date-time, or {@code null} if unchanged.
     */
    private LocalDateTime getUpdatedStartDateTime() {
        if (originalTask instanceof Events && startDatePicker.getValue() == null) {
            return ((Events) originalTask).getEndby();
        } else if (taskTypeComboBox.getValue().equalsIgnoreCase(TaskType.EVENT.name())) {
            return startDatePicker.getValue().atStartOfDay();
        } else {
            return null;
        }
    }

    /**
     * Gets the updated end date-time if modified.
     *
     * @return The new end date-time, or {@code null} if unchanged.
     */
    private LocalDateTime getUpdatedEndDateTime() {
        if (originalTask instanceof Events && endDatePicker.getValue() == null) {
            return ((Events) originalTask).getEndby();
        } else if (taskTypeComboBox.getValue().equalsIgnoreCase(TaskType.EVENT.name())) {
            return endDatePicker.getValue().atStartOfDay();
        } else {
            return null;
        }
    }

    /**
     * Gets the updated due date-time if modified.
     *
     * @return The new due date-time, or {@code null} if unchanged.
     */
    private LocalDateTime getUpdatedDueDateTime() {
        if (originalTask instanceof DeadLine && dueDatePicker.getValue() == null) {
            return ((DeadLine) originalTask).getDueby();
        } else if (taskTypeComboBox.getValue().equalsIgnoreCase(TaskType.DEADLINE.name())) {
            return dueDatePicker.getValue().atStartOfDay();
        } else {
            return null;
        }
    }

    /**
     * Retrieves the task type based on the given task instance.
     *
     * @param task The task whose type is to be determined.
     * @return The corresponding {@link TaskType}, or {@code null} if unrecognized.
     */
    private TaskType getTaskType(Task task) {
        if (task instanceof Events) {
            return TaskType.EVENT;
        }
        if (task instanceof DeadLine) {
            return TaskType.DEADLINE;
        }
        if (task instanceof ToDo) {
            return TaskType.TODO;
        }
        return null;
    }

    /**
     * Closes the update task dialog window.
     */
    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
