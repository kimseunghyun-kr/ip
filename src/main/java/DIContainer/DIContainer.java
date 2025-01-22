package DIContainer;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DIContainer {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Class<?>> registrations = new HashMap<>();
    private final Map<Class<?>, Set<Class<?>>> dependencyGraph = new HashMap<>();

    public void register(Class<?> type) {
        registrations.put(type, type);
        buildDependencyGraph(type);
    }

    public <T> T resolve(Class<T> type) {
        if (!registrations.containsKey(type)) {
            throw new IllegalStateException("Type not registered: " + type.getName());
        }
        return type.cast(instances.computeIfAbsent(type, this::createInstance));
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
        try {
            Constructor<?> constructor = type.getConstructors()[0];
            for (Class<?> dependency : constructor.getParameterTypes()) {
                dependencies.add(dependency);
                buildDependencyGraph(dependency); // Build recursively
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze dependencies for: " + type, e);
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
}
