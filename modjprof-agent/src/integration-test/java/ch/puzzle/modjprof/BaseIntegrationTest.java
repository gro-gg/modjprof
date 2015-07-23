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
package ch.puzzle.modjprof;

import static ch.puzzle.modjprof.TestConstants.AGENT_JAR_LOCATION;
import static ch.puzzle.modjprof.TestConstants.APPLICATION_JAR_LOCATION;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseIntegrationTest {

    private static final boolean printOutput = false;

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

    protected BufferedReader executeAndReadOutput(String[] args, String agentArgs) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(buildCommandLine(args, agentArgs));
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        p.waitFor();
        return reader;
    }

    private List<String> buildCommandLine(String[] args, String agentArgs) throws IOException {
        File applicationJar = new File(APPLICATION_JAR_LOCATION);
        File agentJar = new File(AGENT_JAR_LOCATION);
        List<String> command = new ArrayList<String>();
        command.add("java");
        command.add("-javaagent:" + agentJar.getCanonicalPath());
        if (agentArgs != null) {
            command.add("=");
            command.add(agentArgs);
        }
        command.addAll(Arrays.asList(args));
        command.add("-jar");
        command.add(applicationJar.getCanonicalPath());
        //        System.err.println("executing: " + command.toString());
        return command;
    }

}
