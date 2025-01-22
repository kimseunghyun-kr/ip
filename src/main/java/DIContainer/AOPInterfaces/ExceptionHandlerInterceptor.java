package DIContainer.AOPInterfaces;

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
        System.err.println("EXCEPTION: Caught exception in method " + method.getName() + ": " + throwable.getMessage());
    }
}

