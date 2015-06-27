package ch.puzzle.modjprof;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import ch.puzzle.modjprof.classloader.AgentClassLoader;

public class Agent {

    private static ClassLoader agentClassLoader;
    private static final String TRC_FILE_DIR = "/tmp/";
    private static final String TRC_FILE = TRC_FILE_DIR + "modjprof_%d.trc";
    private static final String CLASS_FILE_TRANSFORMER_CLASS = "ch.puzzle.modjprof.instrumentation.ASMClassFileTransformer";

    static {
        System.err.println("*** " + Agent.class.getSimpleName() + " loaded by "
                + Agent.class.getClassLoader().getClass().getSimpleName());
    }

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        deleteAllTraceFiles();

        agentClassLoader = new AgentClassLoader();
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
}
