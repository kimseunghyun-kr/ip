package dicontainer.aopinterfaces;

import java.lang.reflect.Method;

/**
 * Intercepts Log annotated classes to inject new behaviour by proxy
 *
 * @author kimseunghyun-kr
 * @since v0.1-cli
 */
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
