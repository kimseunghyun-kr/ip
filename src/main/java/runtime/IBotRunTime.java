package runtime;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;
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
