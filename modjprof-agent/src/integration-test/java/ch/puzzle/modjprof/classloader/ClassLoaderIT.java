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
package ch.puzzle.modjprof.classloader;

import static ch.puzzle.modjprof.TestConstants.APPLICATION_JAR;
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

public class ClassLoaderIT extends BaseIntegrationTest {

    @Test
    public void shouldLoadClassesFromDiffrentFiles() throws IOException, InterruptedException {
        File configFile = new File("target/modjprof.properties");
        String agentArguments = "config=" + configFile.getCanonicalPath();
        BufferedReader reader = executeAndReadOutput(new String[] { "-verbose:class" }, agentArguments);
        List<String> classLoadedFrom = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("[Loaded org.objectweb.asm.ClassReader from")) {
                classLoadedFrom.add(extractJarName(line));
            }
        }

        assertThat(classLoadedFrom.size(), is(2));
        assertThat(classLoadedFrom, containsInAnyOrder("asm-5.0.4.jar", APPLICATION_JAR));
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
