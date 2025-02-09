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

    public void initialize() {
        taskTypeComboBox.setItems(FXCollections.observableArrayList("Event", "Deadline", "ToDo"));
        taskTypeComboBox.setOnAction(event -> updateVisibleFields());
    }

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

    private void resetFields() {
        startDatePicker.setVisible(false);
        endDatePicker.setVisible(false);
        dueDatePicker.setVisible(false);

        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        dueDatePicker.setValue(null);
    }

    @FXML
    private void handleSave() {
        if (originalTask == null) {
            return;
        }

        TaskUpdateDao updateDao = buildTaskUpdateDao();
        taskController.updateTask(taskId, updateDao);

        closeDialog();
    }

    private TaskUpdateDao buildTaskUpdateDao() {
        return TaskUpdateDao.builder()
                .taskType(getUpdatedTaskType())
                .name(getUpdatedName())
                .startDate(getUpdatedStartDateTime())
                .endDate(getUpdatedEndDateTime())
                .dueDate(getUpdatedDueDateTime())
                .build();
    }

    private String getUpdatedTaskType() {
        String selectedType = taskTypeComboBox.getValue();
        String currentType = getTaskType(originalTask).name();
        return !Objects.equals(selectedType, currentType) ? selectedType : null;
    }

    private String getUpdatedName() {
        return !Objects.equals(nameField.getText(), originalTask.getName()) ? nameField.getText() : null;
    }

    private LocalDateTime getUpdatedStartDateTime() {
        if (originalTask instanceof Events && startDatePicker.getValue() != null) {
            return ((Events) originalTask).getEndby();
        } else if (taskTypeComboBox.getValue().equalsIgnoreCase(TaskType.EVENT.name())) {
            return startDatePicker.getValue().atStartOfDay();
        } else {
            return null;
        }
    }

    private LocalDateTime getUpdatedEndDateTime() {
        if (originalTask instanceof Events && endDatePicker.getValue() != null) {
            return ((Events) originalTask).getEndby();
        } else if (taskTypeComboBox.getValue().equalsIgnoreCase(TaskType.EVENT.name())) {
            return endDatePicker.getValue().atStartOfDay();
        } else {
            return null;
        }
    }

    private LocalDateTime getUpdatedDueDateTime() {
        if (originalTask instanceof DeadLine && dueDatePicker.getValue() != null) {
            return ((DeadLine) originalTask).getDueby();
        } else if (taskTypeComboBox.getValue().equalsIgnoreCase(TaskType.DEADLINE.name())) {
            return dueDatePicker.getValue().atStartOfDay();
        } else {
            return null;
        }
    }

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

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
