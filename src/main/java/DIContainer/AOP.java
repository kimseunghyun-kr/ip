package DIContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AOP {
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, Interceptor interceptor) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        interceptor.before(target, method, args);
                        Object result = method.invoke(target, args);
                        interceptor.after(target, method, args, result);
                        return result;
                    }
                });
    }
}

@FunctionalInterface
interface Interceptor {
    void before(Object target, Method method, Object[] args);

    default void after(Object target, Method method, Object[] args, Object result) {
        // Default empty implementation
    }
}
