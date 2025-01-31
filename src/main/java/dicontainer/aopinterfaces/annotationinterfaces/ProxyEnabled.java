package dicontainer.aopinterfaces.annotationinterfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class is eligible for proxy-based aspect-oriented programming (AOP).
 * This annotation is typically used to mark classes that require dynamic proxy generation
 * to apply cross-cutting concerns such as logging, transaction management, or security.
 *
 * <p>The {@code implementation} attribute specifies the concrete implementation class
 * that should be used for proxying.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @ProxyEnabled(implementation = MyImplementation.class)
 * public class MyService { ... }
 * }
 * </pre>
 *
 * @author kimseunghyun-kr
 * @since v0.1-cli
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyEnabled {
    /**
     * Specifies the concrete implementation class that should be used for proxying.
     *
     * @return the implementation class
     */
    Class<?> implementation(); // Explicitly specify the implementation
}
