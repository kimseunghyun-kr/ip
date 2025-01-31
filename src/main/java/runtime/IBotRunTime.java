package runtime;

import dicontainer.Proxiable;
import dicontainer.aopinterfaces.annotationinterfaces.ExceptionHandler;
import dicontainer.aopinterfaces.annotationinterfaces.ProxyEnabled;

/**
 * Defines the contract for a bot runtime execution.
 * This interface is proxy-enabled and supports exception handling via AOP.
 * The concrete implementation is {@link BotRunTime}.
 */
@ProxyEnabled(implementation = BotRunTime.class)
public interface IBotRunTime extends Proxiable {

    /**
     * Executes the bot runtime process.
     *
     * @throws RuntimeException if an error occurs during execution.
     */
    @ExceptionHandler
    void run();
}
