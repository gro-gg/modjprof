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
package ch.puzzle.modjprof.agent;

import static ch.puzzle.modjprof.agent.AgentConfiguration.CLASS_FILE_TRANSFORMER_CLASS;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import ch.puzzle.modjprof.classloader.AgentClassLoader;
import ch.puzzle.modjprof.config.AgentProperties;

/**
 * This class is the main class of the java agent containing the premain method.
 * It is responsible to load all other classes of the profiler with the right
 * ClassLoader.
 */
public class Agent {

    @SuppressWarnings("unused")
    private static Agent agentInstance;

    private static ClassLoader agentClassLoader;

    private static AgentTraceFileWriter agentTraceFileWriter;

    private static AgentConfiguration agentConfiguration;

    private static final String JAVAAGENT_VM_PREFIX = "-javaagent:";

    private static final String DEFAULT_PROPERTY_FILE = "modjprof.properties";

    private final static Logger LOGGER = Logger.getLogger(Agent.class.getName());

    public static void premain(String agentArgs, Instrumentation instrumentation) throws Exception {
        agentTraceFileWriter = new AgentTraceFileWriter();
        agentConfiguration = AgentConfiguration.getInstance();
        agentInstance = new Agent(instrumentation);
    }

    protected Agent() {
        agentInstance = this;
    }

    private Agent(Instrumentation instrumentation) throws Exception {
        agentConfiguration.initialize(getConfigFileLocation());
        agentTraceFileWriter.deleteAllTraceFiles();

        URL jarUrl = getJavaagentUrlFromVmArguments();
        agentClassLoader = new AgentClassLoader(jarUrl);
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(agentClassLoader);

        Class<?> cls = agentClassLoader.loadClass(CLASS_FILE_TRANSFORMER_CLASS);
        ClassFileTransformer classFileTransformer = (ClassFileTransformer) cls.getConstructor(String.class).newInstance(
                getConfigFileLocation());
        instrumentation.addTransformer(classFileTransformer);

        Thread.currentThread().setContextClassLoader(previousClassLoader);
    }

    public static void notifyEnterMethod(String methodSignature) {
        agentTraceFileWriter.writeEnterMethod(methodSignature);
    }

    public static void notifyExitMethod() {
        agentTraceFileWriter.writeExitMethod();
    }

    String getConfigFileLocation() throws MalformedURLException, IOException {
        AgentProperties properties = AgentProperties.parsePropertiesString(getJavaagentArguments());
        String property = properties.getProperty("config");
        if (property == null) {
            LOGGER.warning("No config file location found in agent arguments! Use -javaagent:<agent.jar>=config=<config.properties>");
            String dir = new File(getJavaagentLocationFromVmArguments()).getParent();
            File defaultPropertyFile = new File(dir, DEFAULT_PROPERTY_FILE);
            property = defaultPropertyFile.getCanonicalPath();
            LOGGER.warning("Trying to load default config file " + property);
        }
        return property;
    }

    URL getJavaagentUrlFromVmArguments() throws MalformedURLException {
        String javaagent;
        if ((javaagent = getJavaagentLocationFromVmArguments()) != null) {
            return new URL("file:" + javaagent);
        }
        return null;
    }

    String getJavaagentLocationFromVmArguments() {
        String javaagent;
        if ((javaagent = getJavaagentWithArgumentsFromVmArguments()) != null) {
            return javaagent.split("=")[0];
        }
        return null;
    }

    String getJavaagentArguments() {
        String javaagent;
        if ((javaagent = getJavaagentWithArgumentsFromVmArguments()) != null) {
            String[] split = javaagent.split("=", 2);
            if (split.length == 2) {
                return split[1];
            }
        }
        return null;
    }

    String getJavaagentWithArgumentsFromVmArguments() {
        for (String argument : getVmArguments()) {
            if (argument.startsWith(JAVAAGENT_VM_PREFIX)) {
                return argument.substring(JAVAAGENT_VM_PREFIX.length());
            }
        }
        return null;
    }

    List<String> getVmArguments() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments();
    }
}
