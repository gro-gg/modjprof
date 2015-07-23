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
package ch.puzzle.modjprof;

import static ch.puzzle.modjprof.AgentConstants.CLASS_FILE_TRANSFORMER_CLASS;
import static ch.puzzle.modjprof.AgentConstants.TRC_FILE;
import static ch.puzzle.modjprof.AgentConstants.TRC_FILE_DIR;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;

import ch.puzzle.modjprof.classloader.AgentClassLoader;

public class Agent {

    private static ClassLoader agentClassLoader;

    private static final String JAVAAGENT_VM_PREFIX = "-javaagent:";

    //    static {
    //        System.err.println("*** " + Agent.class.getSimpleName() + " loaded by "
    //                + Agent.class.getClassLoader().getClass().getSimpleName());
    //    }

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        deleteAllTraceFiles();

        URL jarUrl = getJavaagentUrlFromVmArguments();
        agentClassLoader = new AgentClassLoader(jarUrl);
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(agentClassLoader);

        Class<?> cls = agentClassLoader.loadClass(CLASS_FILE_TRANSFORMER_CLASS);
        ClassFileTransformer classFileTransformer = (ClassFileTransformer) cls.getConstructor().newInstance();
        inst.addTransformer(classFileTransformer);

        Thread.currentThread().setContextClassLoader(previousClassLoader);
    }

    public static void notifyEnterMethod(String methodSignature) {
        writeMethodCallLine(">", methodSignature);
    }

    public static void notifyExitMethod() {
        writeMethodCallLine("<");
    }

    private static void writeMethodCallLine(String flowPattern) {
        writeMethodCallLine(flowPattern, null);
    }

    private static void writeMethodCallLine(String flowPattern, String methodSignature) {
        long threadId = Thread.currentThread().getId();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(String.format(TRC_FILE, threadId)), true), true);
            writer.println(buildMethodCallLineString(flowPattern, methodSignature));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    private static String buildMethodCallLineString(String flowPattern, String methodSignature) {
        StringBuffer sb = new StringBuffer();
        sb.append(flowPattern);
        sb.append(" ").append(getMilliString());
        if (methodSignature != null) {
            sb.append(" ").append(methodSignature);
        }
        return sb.toString();
    }

    private static String getMilliString() {
        long nanoTime = System.nanoTime();
        int pos = 6;
        String x = Long.toString(nanoTime);
        x = x.substring(0, x.length() - pos) + "." + x.substring(x.length() - pos, x.length());
        return x;
    }

    private static void deleteAllTraceFiles() {
        File[] matchingFiles = (new File(TRC_FILE_DIR)).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("modjprof_") && pathname.getName().endsWith(".trc");
            }
        });
        for (int i = 0; i < matchingFiles.length; i++) {
            matchingFiles[i].delete();
        }
    }

    private static URL getJavaagentUrlFromVmArguments() throws MalformedURLException {
        // find the agent location
        String javaagent = null;
        for (String argument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (argument.startsWith(JAVAAGENT_VM_PREFIX)) {
                javaagent = argument.substring(JAVAAGENT_VM_PREFIX.length());
            }
        }
        return new URL("file:" + javaagent);
    }
}
