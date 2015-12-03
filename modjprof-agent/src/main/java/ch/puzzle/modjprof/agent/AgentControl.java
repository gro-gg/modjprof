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

import java.io.File;
import java.util.logging.Logger;

/**
 * The class AgentControl can be used to control the java agent at runtime. The
 * agent can be enabled and disabled for one or all threads. The agent can be
 * instructed to return a list of all trace files or to delete all trace files.
 */
public class AgentControl {

    private final static Logger LOGGER = Logger.getLogger(AgentControl.class.getName());

    /*
     * lazy thread safe singleton
     */
    private static final class LazyHolder {
        private static final AgentControl INSTANCE = new AgentControl();
    }

    private AgentControl() {
    }

    /**
     * Returns a singleton instance of <tt>AgentControl</tt>
     *
     * @return the singleton instance of AgentControl
     */
    public static AgentControl getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Enables the java agent for all threads.
     */
    public void startAgent() {
        AgentConfiguration.getInstance().enableProfiler();
        LOGGER.info("startAgent() called");
    }

    /**
     * Disables the java agent for all threads.
     */
    public void stopAgent() {
        AgentConfiguration.getInstance().disableProfiler();
        LOGGER.info("stopAgent() called");
    }

    /**
     * Returns an Array of all stored trace files.
     *
     * @return Array of {@link File} containing references to all stored trace
     *         files.
     */
    public long[] getAllTraceFileThreadIds() {
        LOGGER.info("listTraceFiles() called");
        AgentTraceFileWriter traceFileWriter = new AgentTraceFileWriter();
        return traceFileWriter.getAllTraceFileThreadIds();
    }

    /**
     * Returns a string representing the trace files on the server. The string
     * will contain a <tt>%d</tt> which should be replaced with the thread id.
     *
     * @return {@link String}
     */
    public String getTraceFileFormatableString() {
        LOGGER.info("getTraceFileFormatableString() called");
        return AgentConfiguration.getInstance().getTraceFileString();
    }

    /**
     * Deletes all trace files on the server.
     */
    public void deleteAllTraceFile() {
        LOGGER.info("deleteAllTraceFiles() called");
        AgentTraceFileWriter traceFileWriter = new AgentTraceFileWriter();
        traceFileWriter.deleteAllTraceFiles();
    }

    /**
     * Enables the java agent for one threads.
     *
     * @param threadId the id of thread
     */
    public void enableThread(long threadId) {
        AgentConfiguration.getInstance().addThread(threadId);
        LOGGER.info("enableThread(" + threadId + ") called");
    }

    /**
     * Disables the java agent for one threads.
     *
     * @param threadId the id of thread
     */
    public void disableThread(long threadId) {
        AgentConfiguration.getInstance().removeThread(threadId);
        LOGGER.info("disableThread(" + threadId + ") called");
    }
}
