package ch.puzzle.modjprof.classloader;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;

public class AgentClassLoader extends URLClassLoader {

    //    static {
    //        System.err.println("*** " + AgentClassLoader.class.getSimpleName() + " loaded by "
    //                + AgentClassLoader.class.getClassLoader().getClass().getSimpleName());
    //    }

    public AgentClassLoader() {
        // set the parent class loader
        super(new URL[] {}, AgentClassLoader.class.getClassLoader());

        try {
        // add this jar to search path
            // TODO: AgentClassLoader.class.getProtectionDomain().getCodeSource() is null on Wildfly
            //        URL jarUrl = AgentClassLoader.class.getProtectionDomain().getCodeSource().getLocation();
            URL jarUrl = new URL("file:/tmp/modjprof-agent-0.0.1-SNAPSHOT.jar");
        System.err.println(jarUrl.toString());
        addURL(jarUrl);

        // add all dependencies
            if (jarUrl.getProtocol().equals("file") && jarUrl.toString().endsWith(".jar")) {
                jarUrl = new URL("jar:" + jarUrl.toString() + "!/");
            }
            JarURLConnection urlConnection = (JarURLConnection) jarUrl.openConnection();
            File parentDirectory = new File(urlConnection.getJarFileURL().getPath()).getParentFile();

            Properties classpathProperties = new Properties();
            classpathProperties.load(new URL(jarUrl, "/classpath.properties").openStream());
            String[] classPath = ((String) classpathProperties.get("classpath")).split(":");

            for (String file : classPath) {
                addURL(new File(parentDirectory, file).toURI().toURL());
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot build classpath", e);
        }

        for (URL url : getURLs()) {
            System.err.println("loading jar: " + url);
        }
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
