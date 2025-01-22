package DIContainer;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.AOPInterfaces.Interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class DIContainer {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Class<?>> registrations = new HashMap<>();
    private final Map<Class<? extends Annotation>, Interceptor> interceptors = new HashMap<>();
    private final Map<Class<?>, Set<Class<?>>> dependencyGraph = new HashMap<>();
    private final Set<Class<?>> proxyEnabledClasses = new HashSet<>();


    public void register(Class<?> type) {
        // Ensure the interface is annotated with @ProxyEnabled
        if (type.isInterface() && type.isAnnotationPresent(ProxyEnabled.class)) {
            // Get the implementation class from the annotation
            proxyEnabledClasses.add(type);
            ProxyEnabled proxyEnabled = type.getAnnotation(ProxyEnabled.class);
            Class<?> implementation = proxyEnabled.implementation();
            // Map the interface to the implementation
            registrations.put(type, implementation);
            type = implementation; // Register the implementation
        }


        registrations.put(type, type);
        buildDependencyGraph(type);
    }

    private Class<?> findImplementation(Class<?> iface) {
        // Look for a class in the registrations that implements the given interface
        return registrations.values().stream()
                .filter(clazz -> iface.isAssignableFrom(clazz) && !clazz.isInterface())
                .findFirst()
                .orElse(null);
    }


    public void registerInterceptor(Class<? extends Annotation> annotation, Interceptor interceptor) {
        interceptors.put(annotation, interceptor);
    }

    public <T> T resolve(Class<T> type) {
        if (!registrations.containsKey(type)) {
            throw new IllegalStateException("Type not registered: " + type.getName());
        }

        // Create or retrieve the instance
        Object instance = instances.computeIfAbsent(type, this::createInstance);

        // Apply proxy only if the class is marked as proxy-enabled
        if (proxyEnabledClasses.contains(type) && !interceptors.isEmpty()) {
            Class<?> implementationClass = registrations.get(type);
            Class<?>[] interfaces = getAllInterfaces(implementationClass);
            instance = AOP.createProxy(instance, interceptors, interfaces);
        }

        return type.cast(instance);
    }

    private Object createInstance(Class<?> type) {
        try {
            Constructor<?> constructor = registrations.get(type).getConstructors()[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] dependencies = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                dependencies[i] = resolve(paramTypes[i]); // Recursively resolve dependencies
            }

            return constructor.newInstance(dependencies);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance for type: " + type, e);
        }
    }

    private void buildDependencyGraph(Class<?> type) {
        if (dependencyGraph.containsKey(type)) {
            return; // Graph already built for this type
        }

        Set<Class<?>> dependencies = new HashSet<>();

        if (type.isInterface()) {
            // If it's an interface, we can't analyze its dependencies directly.
            // We'll assume the implementation will be resolved later.
            Class<?> implementation = registrations.get(type);
            System.out.println(implementation);
            if (implementation == null) {
                throw new RuntimeException("No implementation registered for interface: " + type.getName());
            }
            buildDependencyGraph(implementation);
        } else {
            try {
                Constructor<?> constructor = type.getConstructors()[0]; // Assume a single constructor
                for (Class<?> dependency : constructor.getParameterTypes()) {
                    dependencies.add(dependency);
                    buildDependencyGraph(dependency); // Build recursively
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to analyze dependencies for: " + type, e);
            }
        }

        dependencyGraph.put(type, dependencies);
    }

    public void initialize() {
        // Sort the types by dependency depth and resolve them in order
        Set<Class<?>> visited = new HashSet<>();
        for (Class<?> type : registrations.keySet()) {
            resolveDependenciesInOrder(type, visited);
        }
    }

    private void resolveDependenciesInOrder(Class<?> type, Set<Class<?>> visited) {
        if (visited.contains(type)) {
            return; // Already processed
        }

        // Resolve dependencies first
        for (Class<?> dependency : dependencyGraph.getOrDefault(type, Set.of())) {
            resolveDependenciesInOrder(dependency, visited);
        }

        // Resolve the current type
        resolve(type);
        visited.add(type);
    }

    private Class<?>[] getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> allInterfaces = new HashSet<>();
        while (clazz != null && clazz != Object.class) {
            for (Class<?> ifc : clazz.getInterfaces()) {
                allInterfaces.add(ifc);
            }
            clazz = clazz.getSuperclass();
        }
        return allInterfaces.toArray(new Class<?>[0]);
    }




}
