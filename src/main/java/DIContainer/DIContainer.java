package DIContainer;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.AOPInterfaces.Interceptor;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;


public class DIContainer {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Class<?>> registrations = new HashMap<>();
    private final Map<Class<? extends Annotation>, Interceptor> interceptors = new HashMap<>();
    private final Map<Class<?>, Set<Class<?>>> dependencyGraph = new HashMap<>();
    private final Set<Class<?>> proxyEnabledClasses = new HashSet<>();


    public void register(Class<?> type) {
        // 1) Check if `type` is an interface or abstract class
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {

            // a) If it's explicitly annotated with @ProxyEnabled, treat that like a primary
            if (type.isAnnotationPresent(ProxyEnabled.class)) {
                ProxyEnabled proxyEnabled = type.getAnnotation(ProxyEnabled.class);
                Class<?> implClass = proxyEnabled.implementation();
                if (implClass == null) {
                    throw new IllegalStateException(
                            "@ProxyEnabled on " + type.getName() + " is missing an implementation class."
                    );
                }

                // Map the interface -> the chosen implementation
                registrations.put(type, implClass);

                // Mark the interface as proxy-eligible
                proxyEnabledClasses.add(type);

                // Build the dependency graph for the chosen impl
                buildDependencyGraph(implClass);

            } else {
                // b) Otherwise, auto-discover implementing classes
                Set<Class<?>> implementations = findImplementations(type);
                if (implementations.isEmpty()) {
                    throw new RuntimeException(
                            "No implementations found for " + type.getName()
                    );
                }

                // If only one, register it directly
                if (implementations.size() == 1) {
                    Class<?> singleImpl = implementations.iterator().next();
                    registrations.put(type, singleImpl);
                    buildDependencyGraph(singleImpl);
                }
                else {
                    // Multiple found. In Spring, you'd look for @Primary among them,
                    // or you could fail or pick the first.
                    // For example:
                    Class<?> primaryImpl = null;
                    for (Class<?> impl : implementations) {
                        if (impl.isAnnotationPresent(ProxyEnabled.class)) {
                            // Let's treat that as "primary".
                            if (primaryImpl != null) {
                                // If multiple have ProxyEnabled, you might want to fail or
                                // pick a single one (like first).
                                throw new RuntimeException(
                                        "Multiple @ProxyEnabled (primary) candidates found for " + type.getName()
                                );
                            }
                            primaryImpl = impl;
                        }
                    }

                    if (primaryImpl == null) {
                        // If none are marked, you can fail or pick one
                        throw new RuntimeException(
                                "Multiple implementations found for " + type.getName() +
                                        ", no @ProxyEnabled or 'primary' annotation to pick from."
                        );
                    }

                    // Register the primary
                    registrations.put(type, primaryImpl);
                    // If you want to also treat it as proxy-eligible:
                    proxyEnabledClasses.add(type);

                    buildDependencyGraph(primaryImpl);
                }
            }
        }
        else {
            // 2) It's a concrete class
            registrations.put(type, type);
            buildDependencyGraph(type);
        }
    }


    private Set<Class<?>> findImplementations(Class<?> interfaceType) {
        Set<Class<?>> implementations = new HashSet<>();

        // Use the ClassLoader to locate classes in the same package as the interface
        String packageName = interfaceType.getPackageName();
        String relativePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(relativePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    implementations.addAll(findClasses(directory, packageName, interfaceType));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find implementations for interface: " + interfaceType.getName(), e);
        }

        return implementations;
    }

    private Set<Class<?>> findClasses(File directory, String packageName, Class<?> interfaceType) {
        Set<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) return classes;

        for (File file : files) {
            if (file.isDirectory()) {
                // Recurse into subdirectories
                classes.addAll(findClasses(file, packageName + "." + file.getName(), interfaceType));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");

                try {
                    Class<?> clazz = Class.forName(className);

                    // Check if the class implements the given interface and is concrete
                    if (interfaceType.isAssignableFrom(clazz) && !clazz.isInterface()
                            && !Modifier.isAbstract(clazz.getModifiers())) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Skip classes that cannot be loaded
                    e.printStackTrace();
                }
            }
        }

        return classes;
    }


    public void registerInterceptor(Class<? extends Annotation> annotation, Interceptor interceptor) {
        interceptors.put(annotation, interceptor);
    }

    public <T> T resolve(Class<T> type) {
        if (!registrations.containsKey(type)) {
            throw new IllegalStateException("Type not registered: " + type.getName());
        }

        Object instance = instances.computeIfAbsent(type, this::createInstance);

        // If type is in proxyEnabledClasses (meaning the interface was annotated),
        // and we have interceptors, build a JDK proxy
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
