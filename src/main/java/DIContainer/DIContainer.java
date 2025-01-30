package DIContainer;

import DIContainer.AOPInterfaces.AnnotationInterfaces.ProxyEnabled;
import DIContainer.AOPInterfaces.Interceptor;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

public class DIContainer {
    // Key = "requested type" (interface or class), Value = "implementation class"
    private final Map<Class<?>, Class<?>> registrations = new HashMap<>();

    // For each class, store constructor-arg overrides
    private final Map<Class<?>, Map<Class<?>, Object>> constructorArgs = new HashMap<>();

    // After creation, store the fully built (and possibly proxied) instances
    private final Map<Class<?>, Object> instances = new HashMap<>();

    // Interceptors keyed by annotation type
    private final Map<Class<? extends Annotation>, Interceptor> interceptors = new HashMap<>();

    // For topological sorting, store: class -> set of dependencies (the classes it needs in its constructor)
    private final Map<Class<?>, Set<Class<?>>> dependencyGraph = new HashMap<>();

    // Keep track of all classes or interfaces that have @ProxyEnabled or map to an @ProxyEnabled interface
    private final Set<Class<?>> proxyEnabledTypes = new HashSet<>();

    // Whether we have completed the creation step
    private boolean initialized = false;

    // ========== Registration Methods ==========

    /**
     * Register a concrete class with specific constructor args.
     */
    public void register(Class<?> type, Object... args) {
        // type is presumably a concrete class
        registrations.put(type, type);
        storeConstructorArgs(type, args);
        // Build dependency graph for 'type'
        buildDependencyGraph(type);
        checkIfProxyEnabled(type);
    }

    /**
     * Register an interface or class without special constructor args.
     */
    public void register(Class<?> type) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            // We see if it's annotated with @ProxyEnabled
            if (type.isAnnotationPresent(ProxyEnabled.class)) {
                ProxyEnabled annot = type.getAnnotation(ProxyEnabled.class);
                Class<?> impl = annot.implementation();
                if (impl == null) {
                    throw new IllegalArgumentException("@ProxyEnabled on " + type + " missing implementation()");
                }
                registrations.put(type, impl);
                checkIfProxyEnabled(type);
                buildDependencyGraph(impl);
            } else {
                // We attempt to auto-discover an impl class
                Set<Class<?>> impls = findImplementations(type);
                if (impls.isEmpty()) {
                    throw new RuntimeException("No implementations found for interface: " + type.getName());
                } else if (impls.size() == 1) {
                    Class<?> singleImpl = impls.iterator().next();
                    registrations.put(type, singleImpl);
                    buildDependencyGraph(singleImpl);
                    checkIfProxyEnabled(singleImpl);
                } else {
                    // multiple possible impls => pick one, or fail
                    // For simplicity, pick the first that might have @ProxyEnabled
                    Class<?> primary = null;
                    for (Class<?> c : impls) {
                        if (c.isAnnotationPresent(ProxyEnabled.class)) {
                            primary = c;
                            break;
                        }
                    }
                    if (primary == null) {
                        throw new RuntimeException(
                                "Multiple implementations found for " + type.getName() +
                                        " and none is @ProxyEnabled to pick as primary"
                        );
                    }
                    registrations.put(type, primary);
                    buildDependencyGraph(primary);
                    checkIfProxyEnabled(primary);
                }
            }
        } else {
            // It's a concrete class
            registrations.put(type, type);
            buildDependencyGraph(type);
            checkIfProxyEnabled(type);
        }
    }

    /**
     * Record constructor arguments for a given class's single constructor.
     */
    private void storeConstructorArgs(Class<?> type, Object... args) {
        Constructor<?> constructor = type.getConstructors()[0];
        if (args.length != constructor.getParameterCount()) {
            throw new IllegalArgumentException(
                    "Constructor arg mismatch for " + type.getName() +
                            ": expected " + constructor.getParameterCount() + " but got " + args.length
            );
        }
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Map<Class<?>, Object> map = new HashMap<>();
        for (int i = 0; i < paramTypes.length; i++) {
            map.put(paramTypes[i], args[i]);
        }
        constructorArgs.put(type, map);
    }

    /**
     * Check if a type or interface is marked with @ProxyEnabled, and store that fact.
     */
    private void checkIfProxyEnabled(Class<?> type) {
        if (type.isAnnotationPresent(ProxyEnabled.class)) {
            proxyEnabledTypes.add(type);
        }
    }

    // ========== Dependency Graph Building ==========

    /**
     * Build or update the dependency graph for the given type.
     * If it's an interface, we look up its chosen implementation.
     */
    private void buildDependencyGraph(Class<?> type) {
        if (dependencyGraph.containsKey(type)) {
            // Already processed
            return;
        }
        // If it's an interface, get the chosen impl
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            Class<?> impl = registrations.get(type);
            if (impl == null) {
                throw new RuntimeException("No implementation found for " + type.getName());
            }
            // Recursively build graph for the impl
            buildDependencyGraph(impl);
            // We store an empty set for the interface itself, or you might omit
            dependencyGraph.put(type, Collections.emptySet());
            return;
        }

        // It's a concrete class => find its constructor dependencies
        Constructor<?> constructor = type.getConstructors()[0];
        Set<Class<?>> deps = new HashSet<>();
        Class<?>[] paramTypes = constructor.getParameterTypes();

        // Possibly skip param if we have a direct constructorArg
        Map<Class<?>, Object> argsMap = constructorArgs.getOrDefault(type, new HashMap<>());

        for (Class<?> paramType : paramTypes) {
            // If paramType isn't satisfied by a direct argument, it's a real dependency
            if (!argsMap.containsKey(paramType)) {
                deps.add(paramType);
                // We also want to build their graph
                buildDependencyGraph(paramType);
            }
        }
        dependencyGraph.put(type, deps);
    }

    /**
     * Finds all concrete classes that implement a given interface in the same package.
     */
    private Set<Class<?>> findImplementations(Class<?> iface) {
        Set<Class<?>> results = new HashSet<>();
        String pkgName = iface.getPackageName();
        String path = pkgName.replace('.', '/');
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = cl.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File dir = new File(resource.getFile());
                if (dir.exists()) {
                    results.addAll(findClasses(dir, pkgName, iface));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    private Set<Class<?>> findClasses(File dir, String pkgName, Class<?> iface) {
        Set<Class<?>> found = new HashSet<>();
        if (!dir.exists()) return found;
        File[] files = dir.listFiles();
        if (files == null) return found;

        for (File f : files) {
            if (f.isDirectory()) {
                found.addAll(findClasses(f, pkgName + "." + f.getName(), iface));
            } else if (f.getName().endsWith(".class")) {
                String clsName = pkgName + "." + f.getName().replace(".class", "");
                try {
                    Class<?> c = Class.forName(clsName);
                    if (iface.isAssignableFrom(c)
                            && !c.isInterface()
                            && !Modifier.isAbstract(c.getModifiers())) {
                        found.add(c);
                    }
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
        }

        return found;
    }

    // ========== Initialization & Topological Sort ==========

    /**
     * Perform a topological sort of the entire dependency graph, then instantiate each in order.
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        // 1) Gather all unique nodes (interfaces and classes)
        Set<Class<?>> allTypes = dependencyGraph.keySet();

        // 2) We do Kahn's Algorithm or a DFS-based approach to get a topological order
        List<Class<?>> topoOrder = topologicalSortDFS(allTypes, dependencyGraph);

        // 3) Build instances in that order
        for (Class<?> type : topoOrder) {
            // We skip interfaces that have no real constructor
            // but we ensure the impl is built. For a plain class, we build it if not built yet.
            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                // The real instance will be created when its impl class is built.
                continue;
            }
            if (!instances.containsKey(type)) {
                // create the real object (and possibly wrap in a proxy)
                createInstanceFor(type);
            }
        }

        // 4) Also ensure any interface not yet in 'instances' is associated with the same proxied instance
        //    if it maps to a known implementation
        for (Map.Entry<Class<?>, Class<?>> e : registrations.entrySet()) {
            Class<?> intfOrClass = e.getKey();
            Class<?> impl = e.getValue();
            if (!instances.containsKey(intfOrClass) && instances.containsKey(impl)) {
                // Link it
                instances.put(intfOrClass, instances.get(impl));
            }
        }

        initialized = true;
    }

    /**
     * Perform a Kahn's Algorithm topological sort to detect cycles and produce an order.
     */
    private List<Class<?>> topologicalSortDFS(Set<Class<?>> nodes,
                                              Map<Class<?>, Set<Class<?>>> graph) {
        // 'graph[A]' = set of classes that A depends on

        List<Class<?>> sortedList = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>();   // permanent mark
        Set<Class<?>> visiting = new HashSet<>();  // temporary mark (detect cycles)

        for (Class<?> node : nodes) {
            if (!visited.contains(node)) {
                dfsVisit(node, graph, visited, visiting, sortedList);
            }
        }
        // The result is in "reverse" topological order if you append after visiting.
        // But we want dependencies first, so we can either reverse at the end or insert at the front.
        // Let's reverse at the end:
        Collections.reverse(sortedList);
        return sortedList;
    }

    private void dfsVisit(Class<?> current,
                          Map<Class<?>, Set<Class<?>>> graph,
                          Set<Class<?>> visited,
                          Set<Class<?>> visiting,
                          List<Class<?>> sortedList) {
        if (visiting.contains(current)) {
            // We found a back edge => cycle
            throw new RuntimeException("Cycle detected in dependency graph at: " + current.getName());
        }
        if (visited.contains(current)) {
            // Already fully processed this node
            return;
        }

        // Mark 'current' as in progress
        visiting.add(current);

        // Visit all dependencies (the classes that 'current' depends on)
        Set<Class<?>> dependencies = graph.getOrDefault(current, Collections.emptySet());
        for (Class<?> dep : dependencies) {
            if (!visited.contains(dep)) {
                dfsVisit(dep, graph, visited, visiting, sortedList);
            }
        }

        // Mark 'current' as fully visited
        visiting.remove(current);
        visited.add(current);

        // Add to the result list. We'll reverse later.
        sortedList.add(current);
    }


    // ========== Instantiation & AOP Proxy Wrapping ==========

    /**
     * Create (and store) a single instance for the given concrete class, respecting constructor args,
     * and possibly wrapping with a JDK proxy if needed.
     */
    private void createInstanceFor(Class<?> implClass) {
        // If already created, skip
        if (instances.containsKey(implClass)) {
            return;
        }

        // 1) Actually instantiate the object
        Object realObj = instantiate(implClass);

        // 2) Check if we need a proxy for it
        //    This is the "unified proxy" step: if this impl or any interface mapped to it is @ProxyEnabled
        if (requiresProxy(implClass)) {
            // gather all relevant interfaces
            Class<?>[] allIfaces = computeAllInterfacesFor(implClass);
            Object proxy = AOP.createProxy(realObj, interceptors, allIfaces);

            // store the proxy
            instances.put(implClass, proxy);

            // also store under any interface keys
            for (Map.Entry<Class<?>, Class<?>> e : registrations.entrySet()) {
                if (e.getValue().equals(implClass)) {
                    instances.put(e.getKey(), proxy);
                }
            }
        } else {
            // no proxy needed
            instances.put(implClass, realObj);

            // store under any interface that points here
            for (Map.Entry<Class<?>, Class<?>> e : registrations.entrySet()) {
                if (e.getValue().equals(implClass)) {
                    instances.put(e.getKey(), realObj);
                }
            }
        }
    }

    /**
     * Actually calls the no-arg or single constructor to build the real instance.
     */
    private Object instantiate(Class<?> implClass) {
        try {
            Constructor<?> constructor = implClass.getConstructors()[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            Map<Class<?>, Object> directArgs = constructorArgs.getOrDefault(implClass, new HashMap<>());
            for (int i = 0; i < paramTypes.length; i++) {
                if (directArgs.containsKey(paramTypes[i])) {
                    args[i] = directArgs.get(paramTypes[i]);
                } else {
                    // must be a class or interface we already built
                    // or about to build? But topological order ensures it's already built if it was a dependency
                    createIfNotExists(paramTypes[i]);
                    args[i] = instances.get(paramTypes[i]);
                }
            }
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException e) {
            throw new RuntimeException("Failed creating instance for " + implClass, e);
        }
    }

    /**
     * If 'type' isn't built yet, find its impl, create that first (in topological order).
     */
    private void createIfNotExists(Class<?> type) {
        if (instances.containsKey(type)) {
            return;
        }
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            Class<?> impl = registrations.get(type);
            // ensure that impl is created
            if (impl != null) {
                if (!instances.containsKey(impl)) {
                    createInstanceFor(impl);
                }
                // link it
                instances.put(type, instances.get(impl));
            } else {
                throw new RuntimeException("No impl for " + type.getName());
            }
        } else {
            // it's a concrete class
            createInstanceFor(type);
        }
    }

    /**
     * Compute whether the given impl class or any of its mapped interfaces is @ProxyEnabled.
     */
    private boolean requiresProxy(Class<?> implClass) {
        // If the impl class itself is annotated with ProxyEnabled, yes
        if (implClass.isAnnotationPresent(ProxyEnabled.class)) {
            return true;
        }
        // If the container flagged it
        if (proxyEnabledTypes.contains(implClass)) {
            return true;
        }
        // If any interface that maps to this impl is @ProxyEnabled
        for (Map.Entry<Class<?>, Class<?>> e : registrations.entrySet()) {
            if (e.getValue().equals(implClass)) {
                Class<?> intf = e.getKey();
                if (intf.isAnnotationPresent(ProxyEnabled.class) || proxyEnabledTypes.contains(intf)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gather all interfaces from the class itself plus any container-registered interfaces that map to it.
     */
    private Class<?>[] computeAllInterfacesFor(Class<?> implClass) {
        Set<Class<?>> result = new HashSet<>();
        // 1) All directly implemented interfaces from the class hierarchy
        Class<?> c = implClass;
        while (c != null && c != Object.class) {
            for (Class<?> i : c.getInterfaces()) {
                result.add(i);
            }
            c = c.getSuperclass();
        }
        // 2) All interfaces in 'registrations' that point to implClass
        for (Map.Entry<Class<?>, Class<?>> e : registrations.entrySet()) {
            if (e.getValue().equals(implClass) && e.getKey().isInterface()) {
                result.add(e.getKey());
            }
        }
        return result.toArray(new Class<?>[0]);
    }

    // ========== Public Resolution ==========

    public <T> T resolve(Class<T> type) {
        if (!initialized) {
            throw new IllegalStateException("Container not initialized yet");
        }
        if (!instances.containsKey(type)) {
            throw new RuntimeException("No instance found for " + type.getName());
        }
        return type.cast(instances.get(type));
    }

    public void registerInterceptor(Class<? extends Annotation> annotation, Interceptor interceptor) {
        interceptors.put(annotation, interceptor);
    }
}
