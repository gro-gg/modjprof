package ch.puzzle.modjprof;

public class AgentClassLoader extends ClassLoader {

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
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
                    // ignore
                }
            }

            // if we could not find it, delegate to parent
            // Note that we don't attempt to catch any ClassNotFoundException
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

            return c;
        }
    }

}
