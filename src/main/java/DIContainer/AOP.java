package DIContainer;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.Interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;

public class AOP {
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target,
                                    Map<Class<? extends Annotation>, Interceptor> interceptors,
                                    Class<?>... interfacesToProxy) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfacesToProxy,
                (proxy, method, args) -> {
                    // 1) Possibly run "before" if method has an annotation we track
                    for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                        Class<? extends Annotation> annoClass = entry.getKey();
                        if (method.isAnnotationPresent(annoClass)) {
                            entry.getValue().before(target, method, args);
                        }
                    }

                    Object result = null;
                    try {
                        result = method.invoke(target, args);

                        // 2) "after" logic if annotated
                        for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                            if (method.isAnnotationPresent(entry.getKey())) {
                                entry.getValue().after(target, method, args, result);
                            }
                        }
                        return result;
                    } catch (InvocationTargetException e) {
                        // 3) Exception logic
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        for (Map.Entry<Class<? extends Annotation>, Interceptor> entry : interceptors.entrySet()) {
                            if (method.isAnnotationPresent(entry.getKey())) {
                                entry.getValue().onException(target, method, args, cause);
                            }
                        }
                        // Possibly do fallback logic if method is annotated with e.g. @ExceptionHandler
                        ExceptionHandler exHandler = method.getAnnotation(ExceptionHandler.class);
                        if (exHandler != null) {
                            Class<?> fallbackClazz = exHandler.returnsDefault();
                            if (!Void.class.equals(fallbackClazz) && !void.class.equals(fallbackClazz)) {
                                return createFallback(fallbackClazz);
                            }
                        }
                        // Otherwise rethrow or return null
                        throw cause;
                    }
                }
        );
    }

    private static Object createFallback(Class<?> fallbackClass) {
        if (fallbackClass.isAssignableFrom(Optional.class)) {
            return Optional.empty();
        }
        try {
            return fallbackClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
