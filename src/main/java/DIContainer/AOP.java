package DIContainer;

import DIContainer.AOPInterfaces.Interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class AOP {
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target,
                                    Map<Class<? extends Annotation>, Interceptor> interceptors,
                                    Class<?>... interfacesToProxy) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfacesToProxy, // the target's interfaces
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 1) Call 'before' on matching interceptors
                        for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                            // If method is annotated with that annotation
                            if (method.isAnnotationPresent(entry.getKey())) {
                                entry.getValue().before(target, method, args);
                            }
                        }

                        Object result = null;
                        try {
                            // 2) Invoke the real method
                            result = method.invoke(target, args);

                            // 3) Call 'after' on matching interceptors
                            for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                                if (method.isAnnotationPresent(entry.getKey())) {
                                    entry.getValue().after(target, method, args, result);
                                }
                            }
                            return result;
                        } catch (InvocationTargetException ex) {
                            // 4) If the real method threw an exception, call 'onException' on matching interceptors
                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                                if (method.isAnnotationPresent(entry.getKey())) {
                                    entry.getValue().onException(target, method, args, cause);
                                }
                            }
                            // Re-throw the original cause to propagate
                            throw cause;
                        }
                    }
                }
        );
    }
}
