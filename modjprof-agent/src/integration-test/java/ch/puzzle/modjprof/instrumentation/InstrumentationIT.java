package ch.puzzle.modjprof.instrumentation;

import static ch.puzzle.modjprof.AgentConstants.TRC_FILE;
import static org.apache.commons.io.Charsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import ch.puzzle.modjprof.BaseIntegrationTest;

public class InstrumentationIT extends BaseIntegrationTest {

    private static final String EXPECTED_TRC_FILE = "ch/puzzle/modjprof/instrumentation/profiler.trc";

    @Test
    public void testExecute() throws IOException, InterruptedException {
        assertThat(execute(new String[] {}), is(equalTo(0)));

        long threadId = Thread.currentThread().getId();
        File file = new File(getClass().getClassLoader().getResource(EXPECTED_TRC_FILE).getFile());
        List<String> expectedLines = FileUtils.readLines(file);
        List<String> resultLines = FileUtils.readLines(new File(String.format(TRC_FILE, threadId)), UTF_8);

        assertThat(expectedLines.size(), is(equalTo(resultLines.size())));
        for (int i = 0; i < expectedLines.size(); i++) {
            assertThat(removeTimestamp(resultLines.get(i)), is(removeTimestamp(expectedLines.get(i))));
        }
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

