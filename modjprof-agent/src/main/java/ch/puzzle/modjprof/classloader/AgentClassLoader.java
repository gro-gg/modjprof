/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */
package ch.puzzle.modjprof.classloader;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;

public class AgentClassLoader extends URLClassLoader {

    public AgentClassLoader(URL jarUrl) {
        // set the parent class loader
        super(new URL[] {}, AgentClassLoader.class.getClassLoader());

        try {
            // add this jar to search path
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
            log("added to class loader class path: " + url);
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
                getParentClassLoader().loadClass(name);
                log("loading of class " + name + " delegated to parent class loader");
            } else {
                log("class " + name + " loaded");
            }

            if (resolve) {
                resolveClass(c);
            }


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
        return getParentClassLoader().getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // Search local (child) resources
        Enumeration<URL> urls = findResources(name);

        if (urls != null && urls.hasMoreElements()) {
            return urls;
        }

        // if we could not find it, delegate to parent
        return getParentClassLoader().getResources(name);
    }

    private ClassLoader getParentClassLoader() {
        if (getParent() != null) {
            return getParent();
        } else {
            return getSystemClassLoader();
        }
    }

    private void log(String message) {
        System.err.println("[main] MAIN " + AgentClassLoader.class.getName() + " - " + message);
    }
}
