package ch.puzzle.modjprof.instrumentation;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class InstrumentationIT extends TestCase {

    private static final String VERSION = "0.0.1-SNAPSHOT";
    private static final String AGENT_JAR = "target/modjprodf-agent-" + VERSION + "-jar-with-dependencies.jar";
    private static final String APPLICATION_JAR = "../sample-application/target/sample-application-" + VERSION + ".jar";

    public void testExecute() throws Exception {
        assertEquals(0, execute(new String[] {}));
    }

    private int execute(String[] args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(buildCommandLine(args));
        // pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);

        Process p = pb.start();
        p.waitFor();
        return p.exitValue();
    }

    private List<String> buildCommandLine(String[] args) throws IOException {
        File jar = new File(APPLICATION_JAR);
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-javaagent:" + AGENT_JAR);
        command.add("-jar");
        command.add(jar.getCanonicalPath());
        command.addAll(Arrays.asList(args));
        return command;
    }

}
