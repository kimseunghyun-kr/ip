package dicontainer;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dicontainer.aopinterfaces.Interceptor;
import dicontainer.aopinterfaces.annotationinterfaces.ProxyEnabled;


/**
 * A lightweight dependency injection (DI) container with support for aspect-oriented programming (AOP).
 * This container allows for registering, resolving, and managing object dependencies while supporting
 * dynamic proxy-based interception using {@link ProxyEnabled} annotations.
 * inspired by Spring.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Automatic dependency resolution using topological sorting.</li>
 *     <li>Support for constructor argument injection.</li>
 *     <li>Dynamic proxy generation for AOP-enabled classes/interfaces.</li>
 *     <li>Interceptor support for methods annotated with custom annotations.</li>
 * </ul>
 *
 * @author kimseunghyun-kr
 * @since v0.1-cli
 */
public class DependencyInjectionContainer {
    /**
     * Maps a requested type (interface or class) to its registered implementation.
     */
    private final Map<Class<?>, Class<?>> registrations = new HashMap<>();
    /**
     * Stores instantiated objects after creation, possibly wrapped with proxies.
     */
    private final Map<Class<?>, Object> instances = new HashMap<>();
    /**
     * Stores constructor argument overrides for registered classes.
     */
    private final Map<Class<?>, Map<Class<?>, Object>> constructorArgs = new HashMap<>();
    /**
     * Dependency graph mapping each class to its required dependencies.
     */
    private final Map<Class<?>, Set<Class<?>>> dependencyGraph = new HashMap<>();
    /**
     * Tracks types that require proxy-based AOP.
     */
    private final Set<Class<?>> proxyEnabledTypes = new HashSet<>();
    /**
     * Maps annotation types to their corresponding interceptors for method-level AOP.
     */
    private final Map<Class<? extends Annotation>, Interceptor> interceptors = new HashMap<>();
    /**
     * Indicates whether the container has been initialized.
     */
    private boolean initialized = false;


    // ========== Registration Methods ==========

    /**
     * Registers a specific implementation class for a given interface, optionally
     * providing constructor arguments. This registration bypasses auto-discovery
     * and does <strong>not</strong> rely on {@link ProxyEnabled} annotations, meaning
     * no automatic proxy wrapping will be applied to this mapping.
     * <p>
     * Usage:
     * <pre>
     *    container.register(MyInterface.class, MyConcreteClass.class, arg1, arg2);
     * </pre>
     * </p>
     *
     * @param interfaceType the interface type to be registered
     * @param concreteType  the concrete class implementing {@code interfaceType}
     * @param args          optional constructor arguments used when instantiating {@code concreteType}
     * @throws IllegalArgumentException if the first parameter is not an interface or
     *                                  the second parameter is not a concrete class
     */
    public void register(Class<?> interfaceType, Class<?> concreteType, Object... args) {
        // Optional: Validate that interfaceType is indeed an interface
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException(
                    "First parameter must be an interface: " + interfaceType.getName()
            );
        }
        // Optional: Validate that concreteType is actually concrete
        if (concreteType.isInterface() || Modifier.isAbstract(concreteType.getModifiers())) {
            throw new IllegalArgumentException(
                    "Second parameter must be a concrete class: " + concreteType.getName()
            );
        }

        registrations.put(interfaceType, concreteType);
        storeConstructorArgs(concreteType, args);


    }

    /**
     * Registers a concrete class along with optional constructor arguments.
     *
     * @param type The class to register.
     * @param args The constructor arguments for the class.
     */
    public void register(Class<?> type, Object... args) {
        // type is presumably a concrete class
        registrations.put(type, type);
        storeConstructorArgs(type, args);
        checkIfProxyEnabled(type);
    }

    /**
     * Registers an interface or abstract class, attempting to find an implementation.
     *
     * @param type The interface or abstract class to register.
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
            } else {
                // We attempt to auto-discover an impl class
                Set<Class<?>> impls = findImplementations(type);
                if (impls.isEmpty()) {
                    throw new RuntimeException("No implementations found for interface: " + type.getName());
                } else if (impls.size() == 1) {
                    Class<?> singleImpl = impls.iterator().next();
                    registrations.put(type, singleImpl);
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
                                "Multiple implementations found for " + type.getName()
                                        + " and none is @ProxyEnabled to pick as primary");
                    }
                    registrations.put(type, primary);
                    checkIfProxyEnabled(primary);
                }
            }
        } else {
            // It's a concrete class
            registrations.put(type, type);
            checkIfProxyEnabled(type);
        }
    }

    /**
     * Stores constructor argument overrides for a registered class.
     *
     * @param type The class type.
     * @param args The constructor arguments.
     */
    private void storeConstructorArgs(Class<?> type, Object... args) {
        Constructor<?> constructor = type.getConstructors()[0];
        Class<?>[] paramTypes = constructor.getParameterTypes();

        // If the user provided more arguments than there are parameters, that's definitely an error:
        if (args.length > paramTypes.length) {
            throw new IllegalArgumentException("Too many constructor args for " + type.getName());
        }

        Map<Class<?>, Object> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            // We'll match each provided arg to paramTypes[i]
            map.put(paramTypes[i], args[i]);
        }
        constructorArgs.put(type, map);
    }


    /**
     * Checks if a class or interface is annotated with {@link ProxyEnabled} and records it.
     *
     * @param type The class to check.
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
     *
     * @param type The class whose dependencies need to be tracked.
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
     *
     * @param iface the interface class whose concrete class designations have to be found
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
        if (!dir.exists()) {
            return found;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return found;
        }

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
        // Build the graph for all registrations:
        for (Class<?> key : registrations.keySet()) {
            Class<?> implClass = registrations.get(key);
            buildDependencyGraph(implClass);
        }
        // 1) Gather all unique nodes (interfaces and classes)
        Set<Class<?>> allTypes = dependencyGraph.keySet();

        // 2) We do Kahn's Algorithm or a DFS-based approach to get a topological order
        List<Class<?>> topoOrder = topologicalSortDfs(allTypes, dependencyGraph);

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
    private List<Class<?>> topologicalSortDfs(Set<Class<?>> nodes,
                                              Map<Class<?>, Set<Class<?>>> graph) {
        // 'graph[A]' = set of classes that A depends on

        List<Class<?>> sortedList = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>(); // permanent mark
        Set<Class<?>> visiting = new HashSet<>(); // temporary mark (detect cycles)

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
     *
     * @param implClass The concrete class to instantiate.
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
            Object proxy = Aop.createProxy(realObj, interceptors, allIfaces);

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
     *
     * @param implClass the classes that will be instantiated stored within the registrations
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
     *
     * @param type the class instance where the topological sort will start
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
     *
     * @param implClass the concrete(implementation) class which is to be checked for
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
     *
     * @param implClass the concrete(implemented) class for which the interfaces are resolved
     */
    private Class<?>[] computeAllInterfacesFor(Class<?> implClass) {
        Set<Class<?>> result = new HashSet<>();
        // 1) All directly implemented interfaces from the class hierarchy
        Class<?> c = implClass;
        while (c != null && c != Object.class) {
            result.addAll(Arrays.asList(c.getInterfaces()));
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

    /**
     * Resolves an instance of the given type.
     *
     * @param type The class type.
     * @param <T>  The type parameter.
     * @return The resolved instance.
     */
    public <T> T resolve(Class<T> type) {
        if (!initialized) {
            throw new IllegalStateException("Container not initialized yet");
        }
        if (!instances.containsKey(type)) {
            throw new RuntimeException("No instance found for " + type.getName());
        }
        return type.cast(instances.get(type));
    }

    /**
     * Registers an interceptor for methods annotated with a given annotation.
     *
     * @param annotation  The annotation type.
     * @param interceptor The interceptor instance.
     */
    public void registerInterceptor(Class<? extends Annotation> annotation, Interceptor interceptor) {
        interceptors.put(annotation, interceptor);
    }
}
