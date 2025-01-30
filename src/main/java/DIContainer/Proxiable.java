package DIContainer;

/**
 * a marker interface to signal proxiability of a class
 * all DI container managed classes must implement this proxiable
 * in order to allow JDK dynamic proxies to work
 * it sucks to be prevented from using CGLIB that can allow subclass based proxies
 * but owelps this is a design choice
 */
public interface Proxiable {
}
