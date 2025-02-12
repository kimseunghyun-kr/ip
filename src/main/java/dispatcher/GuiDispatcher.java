package dispatcher;

import gui.JavaFxLauncher;
import javafx.application.Application;
import lombok.Setter;


/**
 * Dispatches the GUI application by launching JavaFX.
 */
@Setter
public class GuiDispatcher implements IDispatcher {
    private String[] args;

    @Override
    public void run() {
        Application.launch(JavaFxLauncher.class, args);
    }
}
