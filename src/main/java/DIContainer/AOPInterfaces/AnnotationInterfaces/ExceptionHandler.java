package DIContainer.AOPInterfaces.AnnotationInterfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as an exception handler within the DI container's AOP framework.
 * Methods annotated with {@code ExceptionHandler} will be invoked when an exception occurs
 * in the advised method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {
    /**
     * Specifies a default return type in case of an exception.
     * If set to {@code Void.class}, the handler does not return a default value.
     *
     * @return The class type to be returned as a default value.
     */
    Class<?> returnsDefault() default Void.class;
}

