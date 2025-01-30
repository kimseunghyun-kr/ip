package DIContainer.AOPInterfaces;

import exceptions.UserFacingException;

import java.lang.reflect.Method;

public class ExceptionHandlerInterceptor implements Interceptor {
    @Override
    public void before(Object target, Method method, Object[] args) {
        // No-op for exception handling
    }

    @Override
    public void after(Object target, Method method, Object[] args, Object result) {
        // No-op for exception handling
    }

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

