package ch.puzzle.modjprof;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;

public class Profiler {

	private static PrintWriter writer;

	public static void premain(String agentArgs, Instrumentation inst)
			throws IOException {
        writer = new PrintWriter(new FileWriter(new File("/tmp/profiler.trc")),
				true);
		inst.addTransformer(new ASMClassFileTransformer());
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
}
