package DIContainer;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class DIContainer {
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public void register(Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructors()[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] dependencies = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                dependencies[i] = resolve(paramTypes[i]);
            }

            Object instance = constructor.newInstance(dependencies);
            instances.put(type, instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register type: " + type, e);
        }
    }

    public <T> T resolve(Class<T> type) {
        return type.cast(instances.computeIfAbsent(type, t -> {
            throw new IllegalStateException("Type not registered: " + t.getName());
        }));
    }

    public void initialize() {
        // Eagerly initialize all registered components
        for (Class<?> type : instances.keySet()) {
            resolve(type);
        }
    }
}
