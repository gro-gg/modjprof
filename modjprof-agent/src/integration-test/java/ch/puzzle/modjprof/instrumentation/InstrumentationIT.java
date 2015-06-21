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

    private static final String VERSION = "0.0.1-SNAPSHOT";
    private static final String AGENT_JAR = "target/modjprodf-agent-" + VERSION + "-jar-with-dependencies.jar";
    private static final String APPLICATION_JAR = "../sample-application/target/sample-application-" + VERSION + ".jar";

    @Test
    public void testExecute() throws IOException {
        try {
            assertThat(execute(new String[] {}), is(equalTo(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("application terminated");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("ch/puzzle/modjprof/instrumentation/profiler.trc").getFile());
        List<String> expectedLines = FileUtils.readLines(file);
        List<String> resultLines = FileUtils.readLines(new File("/tmp/profiler.trc"), Charsets.UTF_8);
        System.err.println(expectedLines.size());
        assertThat(expectedLines.size(), is(equalTo(resultLines.size())));
        for (int i = 0; i < expectedLines.size(); i++) {
            System.err.println(resultLines.get(i));
            // TODO ignore timestamp
            assertThat(resultLines.get(i), is(expectedLines.get(i)));
        }

    }

    private int execute(String[] args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(buildCommandLine(args));
        // pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);

        Process p = pb.start();
        return p.waitFor();
        //        return p.exitValue();
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

