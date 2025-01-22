package DIContainer.AOPInterfaces;

import DIContainer.AOPInterfaces.Interceptor;

import java.lang.reflect.Method;

public class LoggingInterceptor implements Interceptor {
    @Override
    public void before(Object target, Method method, Object[] args) {
        System.out.println("LOG: Entering method " + method.getName());
    }

    @Override
    public void after(Object target, Method method, Object[] args, Object result) {
        System.out.println("LOG: Exiting method " + method.getName() + ", result: " + result);
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        // Logging does not handle exceptions specifically
    }
}
