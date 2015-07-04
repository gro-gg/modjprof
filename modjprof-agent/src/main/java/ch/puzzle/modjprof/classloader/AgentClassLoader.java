package ch.puzzle.modjprof.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class AgentClassLoader extends URLClassLoader {

    static {
        System.err
.println("*** " + AgentClassLoader.class.getSimpleName() + " loaded by "
                + AgentClassLoader.class.getClassLoader().getClass().getSimpleName());
    }

    public AgentClassLoader() {
        // set the parent class loader and add this jar to search path
        super(new URL[] { AgentClassLoader.class.getProtectionDomain().getCodeSource().getLocation() }, AgentClassLoader.class
                .getClassLoader());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        //Always delegate JDK classes to parent.
        if (name.startsWith("java.") || name.startsWith("javax.")) {
            return super.loadClass(name, resolve);
        }

        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);

            // if not loaded, search the local (child) resources
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException cnfe) {
                    // ClassNotFoundException thrown if class not found
                    // in the search path of this class loader
                }
            }

            // if we could not find it, delegate to parent
            if (c == null) {
                if (getParent() != null) {
                    c = getParent().loadClass(name);
                } else {
                    c = getSystemClassLoader().loadClass(name);
                }
            }

            if (resolve) {
                resolveClass(c);
            }

            System.err.println("===> class " + name + " loaded with AgentClassLoader");

            return c;
        }
    }

    @Override
    public URL getResource(String name) {
        // Search local (child) resources
        URL url = findResource(name);

        if (url != null) {
            return url;
        }

        // if we could not find it, delegate to parent
        return getParent().getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // Search local (child) resources
        Enumeration<URL> urls = findResources(name);

        if (urls != null && urls.hasMoreElements()) {
            return urls;
        }

        // if we could not find it, delegate to parent
        return getParent().getResources(name);
    }
}
