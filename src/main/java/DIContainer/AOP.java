package DIContainer;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ExceptionHandler;
import DIContainer.AOPInterfaces.Interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;

/**
 * Provides dynamic proxy-based aspect-oriented programming (AOP) support.
 * This class allows for method-level interception based on custom annotations.
 *
 * The proxy can apply interceptors for "before", "after", and "onException" logic
 * when a method is annotated with a registered annotation.
 *
 * If a method annotated with {@link ExceptionHandler} throws an exception,
 * a fallback value may be returned based on the specified default return type.
 *
 * @author kimseunghyun-kr
 * @since v0.1-cli
 */
public class AOP {

    /**
     * Creates a dynamic proxy for the given target object.
     * The proxy intercepts method calls based on the provided annotations and applies corresponding interceptors.
     *
     * @param target         The original object being proxied.
     * @param interceptors   A mapping of annotation classes to their corresponding interceptors.
     * @param interfacesToProxy The interfaces that the proxy should implement.
     * @param <T>           The type of the proxied object.
     * @return A proxy instance that applies AOP logic.
     */
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

    /**
     * Creates a fallback instance based on the specified fallback class.
     * If the fallback class is {@link Optional}, an empty optional is returned.
     * Otherwise, an instance is created using the default constructor if available.
     *
     * @param fallbackClass The fallback class specified in the {@link ExceptionHandler} annotation.
     * @return A fallback instance, or {@code null} if instantiation fails.
     */
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
