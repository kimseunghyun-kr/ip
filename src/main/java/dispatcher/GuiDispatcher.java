package dispatcher;

import gui.JavaFxLauncher;
import javafx.application.Application;
import service.ActionHandler;


/**
 * Dispatches the GUI application by launching JavaFX.
 */
public class GuiDispatcher implements IDispatcher {
    private final ActionHandler actionHandler;
    private String[] args;

    public GuiDispatcher(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        JavaFxLauncher.setActionHandler(actionHandler);
        Application.launch(JavaFxLauncher.class, args);
    }
}
