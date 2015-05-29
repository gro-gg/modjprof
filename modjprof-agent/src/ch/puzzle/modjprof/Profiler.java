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
		writer = new PrintWriter(new FileWriter(new File("/tmp/profiler.txt")),
				true);
        System.out.println("Starting the ASM agent...");
		inst.addTransformer(new ASMClassFileTransformer());
	}

	public static void notifyEnterMethod(String methodSignature) {
		writer.println("> " + System.nanoTime() + " " + methodSignature);
	}

	public static void notifyExitMethod() {
		writer.println("< " + System.nanoTime());
	}
}
