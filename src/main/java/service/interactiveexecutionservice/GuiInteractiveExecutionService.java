package service.interactiveexecutionservice;

import java.io.IOException;
import java.util.List;

import gui.components.TaskUpdateDialogController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * GUI-based interactive execution service using JavaFX.
 */
public class GuiInteractiveExecutionService implements InteractiveExecutionService {
    @Override
    public String startInteractiveUpdate(List<String> parameters) {
        if (parameters.isEmpty()) {
            return "Error: No task ID provided for interactive update.";
        }

        try {
            int taskId = Integer.parseInt(parameters.get(0));
            Platform.runLater(() -> openUpdateDialog(taskId));
            return "Interactive update started for Task " + taskId;
        } catch (NumberFormatException e) {
            return "Error: Invalid task ID.";
        }
    }

    @Override
    public String handleInteractiveUpdate(String input) {
        return "Updates processed via GUI.";
    }

    @Override
    public boolean isActiveSession() {
        return false;
    }

    private void openUpdateDialog(int taskId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskUpdateDialog.fxml"));
            Parent root = loader.load();

            TaskUpdateDialogController controller = loader.getController();
            controller.setTaskId(taskId);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Task");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
