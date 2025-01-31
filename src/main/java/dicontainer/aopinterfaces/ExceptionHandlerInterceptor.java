package dicontainer.aopinterfaces;

import java.lang.reflect.Method;

import exceptions.UserFacingException;



/**
 * Handles exceptions thrown during method execution.
 * It distinguishes between user-facing exceptions and system-level exceptions,
 * ensuring appropriate logging and error propagation.
 * If the exception is an instance of {@link UserFacingException}, a user-friendly message is logged.
 * Otherwise, system-level exceptions are logged with a stack trace and rethrown as a {@link RuntimeException}.
 *
 * @author kimseunghyun-kr
 * @since v0.1-cli
 */
public class ExceptionHandlerInterceptor implements Interceptor {
    /**
     * No-op method executed before the target method invocation.
     *
     * @param target the target object on which the method is invoked
     * @param method the method being intercepted
     * @param args   the arguments passed to the method
     */
    @Override
    public void before(Object target, Method method, Object[] args) {
        // No-op for exception handling
    }

    /**
     * No-op method executed after the target method invocation.
     *
     * @param target the target object on which the method was invoked
     * @param method the method being intercepted
     * @param args   the arguments passed to the method
     * @param result the return value of the method execution
     */
    @Override
    public void after(Object target, Method method, Object[] args, Object result) {
        // No-op for exception handling
    }

    /**
     * Handles exceptions thrown during method execution.
     * Differentiates between user-facing exceptions and system-level exceptions.
     *
     * @param target    the target object on which the method was invoked
     * @param method    the method where the exception occurred
     * @param args      the arguments passed to the method
     * @param throwable the exception thrown during execution
     * @throws RuntimeException if the exception is not user-facing
     */
    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        if (throwable instanceof UserFacingException) {
            System.err.println("User-facing exception: " + throwable.getMessage());
            // Optionally log or handle it differently for user feedback
        } else {
            // System-level exceptions are critical and may need a stack trace or alerting
            System.err.println("Unhandled system exception in method: " + method.getName());
            throwable.printStackTrace();
            throw new RuntimeException("An unexpected error occurred. Please try again later.", throwable);
        }
    }
}

