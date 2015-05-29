package ch.puzzle.modjprof;

import java.io.IOException;
import java.lang.instrument.Instrumentation;


public class Profiler {

	public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        System.out.println("Starting modjprof-agent ...");
    }
	
	
}
