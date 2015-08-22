/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */
package ch.puzzle.modjprof.instrumentation;

import static ch.puzzle.modjprof.agent.AgentRuntimeConfiguration.TRC_FILE;
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
        assertThat(execute(new String[] {}, "config=target/modjprof.properties"), is(equalTo(0)));

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

