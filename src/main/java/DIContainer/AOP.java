package DIContainer;

import DIContainer.AOPInterfaces.Interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class AOP {
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, Map<Class<? extends Annotation>, Interceptor> interceptors) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{Proxiable.class}, // Use the marker interface
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                            if (method.isAnnotationPresent(entry.getKey())) {
                                entry.getValue().before(target, method, args);
                            }
                        }

                        Object result = method.invoke(target, args);

                        for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                            if (method.isAnnotationPresent(entry.getKey())) {
                                entry.getValue().after(target, method, args, result);
                            }
                        }

                        return result;
                    }
                });
    }
}
