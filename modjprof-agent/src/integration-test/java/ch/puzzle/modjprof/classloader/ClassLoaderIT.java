package ch.puzzle.modjprof.classloader;

import static ch.puzzle.modjprof.TestConstants.AGENT_JAR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.puzzle.modjprof.BaseIntegrationTest;
import ch.puzzle.modjprof.TestConstants;

public class ClassLoaderIT extends BaseIntegrationTest {

    @Test
    public void shouldLoadClassesFromDiffrentFiles() throws IOException, InterruptedException {
        BufferedReader reader = executeAndReadOutput(new String[] { "-verbose:class" }, null);
        List<String> classLoadedFrom = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("[Loaded org.objectweb.asm.ClassReader from")) {
                classLoadedFrom.add(extractJarName(line));
            }
        }

        //        for (String jar : classLoadedFrom) {
        //            System.err.println(jar);
        //        }

        assertThat(classLoadedFrom.size(), is(2));
        // TODO: add APPLICATION_JAR
        //        assertThat(classLoadedFrom, containsInAnyOrder(AGENT_JAR, TestConstants.APPLICATION_JAR));
        assertThat(classLoadedFrom, containsInAnyOrder(AGENT_JAR, TestConstants.AGENT_JAR));
    }

    private String extractJarName(String line) {
        if (line == null) {
            return null;
        }
        String s = line.substring(1, line.length() - 1);
        File file = new File(Arrays.asList(s.split(" ")).get(3));
        return file.getName();
    }
}
