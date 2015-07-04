package ch.puzzle.modjprof;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseIntegrationTest {

    private static final boolean printOutput = false;
    private static final String VERSION = "0.0.1-SNAPSHOT";
    private static final String AGENT_JAR = "target/modjprodf-agent-" + VERSION + "-jar-with-dependencies.jar";
    private static final String APPLICATION_JAR = "../sample-application/target/sample-application-" + VERSION + ".jar";


    protected int execute(String[] args) throws IOException, InterruptedException {
        return execute(args, null);
    }

    protected int execute(String[] args, String agentArgs) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(buildCommandLine(args, agentArgs));
        if (printOutput) {
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
        }
        Process p = pb.start();
        return p.waitFor();
    }

    private List<String> buildCommandLine(String[] args, String agentArgs) throws IOException {
        File applicationJar = new File(APPLICATION_JAR);
        File agentJar = new File(AGENT_JAR);
        List<String> command = new ArrayList<String>();
        command.add("java");
        command.add("-javaagent:" + agentJar.getCanonicalPath());
        if (agentArgs != null) {
            command.add("=");
            command.add(agentArgs);
        }
        command.add("-jar");
        command.add(applicationJar.getCanonicalPath());
        command.addAll(Arrays.asList(args));
        return command;
    }

}
