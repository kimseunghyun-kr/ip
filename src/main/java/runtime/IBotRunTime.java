package runtime;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.Proxiable;

@ProxyEnabled(implementation = BotRunTime.class)
public interface IBotRunTime extends Proxiable {
    @ExceptionHandler
    public void run();
}
