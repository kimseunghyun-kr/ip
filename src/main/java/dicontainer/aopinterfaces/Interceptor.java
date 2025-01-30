package dicontainer.aopinterfaces;

import java.lang.reflect.Method;

/**
 * Relays groundwork for future Interceptor based implementations
 *
 * @author kimseunghyun-kr
 * @since v0.1-cli
 */
public interface Interceptor {
    /**
     * Invoked before the target method is executed.
     *
     * @param target The target object.
     * @param method The method being called.
     * @param args   The arguments passed to the method.
     */
    void before(Object target, Method method, Object[] args);

    /**
     * Invoked after the target method is executed successfully.
     *
     * @param target The target object.
     * @param method The method being called.
     * @param args   The arguments passed to the method.
     * @param result The result returned by the method.
     */
    void after(Object target, Method method, Object[] args, Object result);

    /**
     * Invoked if an exception occurs during method execution.
     *
     * @param target    The target object.
     * @param method    The method being called.
     * @param args      The arguments passed to the method.
     * @param throwable The exception that was thrown.
     */
    void onException(Object target, Method method, Object[] args, Throwable throwable);
}

