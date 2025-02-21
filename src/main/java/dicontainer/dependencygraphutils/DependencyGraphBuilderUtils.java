package dicontainer.dependencygraphutils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import dicontainer.aopinterfaces.annotationinterfaces.ProxyEnabled;

/**
 * Provides recursive constructor finding capabilities to DependencyInjectionContainer class
 */
public class DependencyGraphBuilderUtils {
    /**
     * Finds all concrete classes that implement a given interface in the same package.
     *
     * @param iface the interface class whose concrete class designations have to be found
     */
    public static Set<Class<?>> findImplementations(Class<?> iface) {
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

    /**
     * Finds all classes in the same package.
     * it is essentially doing a package scan
     * @param iface the interface class whose concrete class designations have to be found
     */
    static Set<Class<?>> findClasses(File dir, String pkgName, Class<?> iface) {
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

    /**
     * Finds all concrete classes that implement a given interface in the same package, specifically for
     * ProxyEnabled classes
     */
    public static Class<?> getConcreteClass(Class<?> type, Set<Class<?>> impls) {
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
        return primary;
    }
}
