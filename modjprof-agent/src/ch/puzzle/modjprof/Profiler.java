package ch.puzzle.modjprof;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;

public class Profiler {

    static {
        System.err.println("*** Profiler loaded by " + Profiler.class.getClassLoader().getClass().getSimpleName());
    }

    private static PrintWriter writer;

    private static ClassLoader agentClassLoader;

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        writer = new PrintWriter(new FileWriter(new File("/tmp/profiler.trc")), true);

        agentClassLoader = (ClassLoader) Class.forName("ch.puzzle.modjprof.AgentClassLoader").newInstance();
        // ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(agentClassLoader);

        Class<?> cls = agentClassLoader.loadClass("ch.puzzle.modjprof.ASMClassFileTransformer");


        ClassFileTransformer classFileTransformer = (ClassFileTransformer) getDefaultConstructor(cls).newInstance();

        inst.addTransformer(classFileTransformer);
    }

    public static void notifyEnterMethod(String methodSignature) {
        writer.println("> " + getMilliString() + " " + methodSignature);
    }

    public static void notifyExitMethod() {
        writer.println("< " + getMilliString());
    }

    private static String getMilliString() {
        long nanoTime = System.nanoTime();
        int pos = 6;
        String x = Long.toString(nanoTime);
        x = x.substring(0, x.length() - pos) + "." + x.substring(x.length() - pos, x.length());
        return x;
    }

    private static Constructor<?> getDefaultConstructor(Class<?> cls) {
        Constructor<?>[] constructors = cls.getConstructors();
        Constructor<?> constructor = null;
        for (int i = 0; i < constructors.length; i++) {
            constructor = constructors[i];
            if (constructor.getGenericParameterTypes().length == 0) {
                break;
            }
        }
        return constructor;
    }
}
