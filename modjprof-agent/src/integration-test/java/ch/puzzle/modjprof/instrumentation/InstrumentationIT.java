package ch.puzzle.modjprof.instrumentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class InstrumentationIT {

    private static final boolean printOutput = false;
    private static final String RESULT_TRC_FILE = "/tmp/profiler.trc";
    private static final String EXPECTED_TRC_FILE = "ch/puzzle/modjprof/instrumentation/profiler.trc";
    private static final String VERSION = "0.0.1-SNAPSHOT";
    private static final String AGENT_JAR = "target/modjprodf-agent-" + VERSION + "-jar-with-dependencies.jar";
    private static final String APPLICATION_JAR = "../sample-application/target/sample-application-" + VERSION + ".jar";

    @Test
    public void testExecute() throws IOException, InterruptedException {
        assertThat(execute(new String[] {}), is(equalTo(0)));

        File file = new File(getClass().getClassLoader().getResource(EXPECTED_TRC_FILE).getFile());
        List<String> expectedLines = FileUtils.readLines(file);
        List<String> resultLines = FileUtils.readLines(new File(RESULT_TRC_FILE), Charsets.UTF_8);

        assertThat(expectedLines.size(), is(equalTo(resultLines.size())));
        for (int i = 0; i < expectedLines.size(); i++) {
            assertThat(removeTimestamp(resultLines.get(i)), is(removeTimestamp(expectedLines.get(i))));
        }
    }

    private int execute(String[] args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(buildCommandLine(args));
        if (printOutput) {
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
        }
        Process p = pb.start();
        return p.waitFor();
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

    private String removeTimestamp(String line) {
        String[] split = line.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (i != 1) {
                sb.append(split[i]);
            }
        }
        return sb.toString();
    }
}

