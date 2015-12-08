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
package ch.puzzle.modjprof.agent;

import static ch.puzzle.modjprof.agent.AgentConfiguration.TRC_FILE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is responsible to write the trace file for each monitored thread.
 * The trace file contains an entry for entering and for leaving a method.
 *
 */
public class AgentTraceFileWriter {

    public void writeEnterMethod(String methodSignature) {
        writeMethodCallLine(">", methodSignature);
    }

    public void writeExitMethod() {
        writeMethodCallLine("<");
    }

    private void writeMethodCallLine(String flowPattern) {
        writeMethodCallLine(flowPattern, null);
    }

    private void writeMethodCallLine(String flowPattern, String methodSignature) {
        long threadId = Thread.currentThread().getId();
        if (AgentConfiguration.getInstance().isProfilerEnabled() || AgentConfiguration.getInstance().isThreadEnabled(threadId)) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter(new File(String.format(TRC_FILE, threadId)), true), true);
                writer.println(buildMethodCallLineString(flowPattern, methodSignature));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private String buildMethodCallLineString(String flowPattern, String methodSignature) {
        StringBuffer sb = new StringBuffer();
        sb.append(flowPattern);
        sb.append(" ").append(getMilliString());
        if (methodSignature != null) {
            sb.append(" ").append(methodSignature);
        }
        return sb.toString();
    }

    private String getMilliString() {
        long nanoTime = System.nanoTime();
        int pos = 6;
        String x = Long.toString(nanoTime);
        x = x.substring(0, x.length() - pos) + "." + x.substring(x.length() - pos, x.length());
        return x;
    }

}
