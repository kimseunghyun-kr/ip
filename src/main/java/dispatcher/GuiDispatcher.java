package dispatcher;

import gui.JavaFxLauncher;
import javafx.application.Application;
import runtime.ActionHandler;


/**
 * Dispatches the GUI application by launching JavaFX.
 */
public class GuiDispatcher implements IDispatcher {
    private String[] args;
    private final ActionHandler actionHandler;

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
