package dicontainer.aopinterfaces;

import java.lang.reflect.Method;

/**
 * Intercepts TransactionalContext Annotated classes to inject new behaviour by proxy
 *
 * @author kimseunghyun-kr
 * @since v0.1-cli
 */
public class TransactionalInterceptor implements Interceptor {
    @Override
    public void before(Object target, Method method, Object[] args) {
        System.out.println("TRANSACTION: Starting transaction for " + method.getName());
    }

    @Override
    public void after(Object target, Method method, Object[] args, Object result) {
        System.out.println("TRANSACTION: Committing transaction for " + method.getName());
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        System.out.println("TRANSACTION: Rolling back transaction for " + method.getName());
    }
}
