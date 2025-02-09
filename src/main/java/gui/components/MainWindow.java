package gui.components;

import static service.CommandExecutionService.TERMSIG;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Setter;
import service.CommandExecutionService;

/**
 * Controller for MainWindow. Provides the layout for the other controls.
 */
public class MainWindow extends AnchorPane {
    public static final String EXITMSG = "Bye Bye see you next time";
    @Setter
    private static CommandExecutionService commandExecutionService;
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/verstappen.jpg"));
    private final Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/amiya.png"));
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    /**
     * initialises the javafx application
     */
    @FXML
    public void initialize() {
        dialogContainer.prefWidthProperty().bind(scrollPane.widthProperty());
        dialogContainer.prefHeightProperty().bind(scrollPane.heightProperty());
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        begin();
    }

    private void begin() {
        dialogContainer.getChildren().addAll(
                DialogBox.getDialogBox("hi this is Amiya, your personal task manager."
                        + " How may i help you today", dukeImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDialogBox(response, dukeImage)
        );
        userInput.clear();
    }

    public String getResponse(String input) {
        String output = commandExecutionService.runCommand(input);
        if (output.equals(TERMSIG)) {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> Platform.exit());
            pause.play();
            return EXITMSG;
        }
        return output;
    }
}