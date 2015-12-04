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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import ch.puzzle.modjprof.config.AgentProperties;
import ch.puzzle.modjprof.config.PropertiesReader;

/**
 * This class includes the runtime configuration of the agent. For example if
 * the profiler is enabled or disabled globally or for a specific thread.
 */
public class AgentConfiguration {

    private final static Logger LOGGER = Logger.getLogger(AgentConfiguration.class.getName());

    static {
        LOGGER.info("class loaded by " + AgentConfiguration.class.getClassLoader());
    }

    /*
     * eager thread safe singleton, only visible in this package
     */
    private static final AgentConfiguration INSTANCE = new AgentConfiguration();

    private AgentConfiguration() {
    }

    static AgentConfiguration getInstance() {
        return INSTANCE;
    }


    public static final String TRC_FILE_DIR = "/tmp/";

    public static final String TRC_FILE = TRC_FILE_DIR + "modjprof_%d.trc";

    public static final String CLASS_FILE_TRANSFORMER_CLASS = "ch.puzzle.modjprof.instrumentation.InstrumentationClassFileTransformer";

    private static boolean profilerEnabled = false;

    private Set<Long> threadsToProfile = new HashSet<Long>();

    void initialize(String propertiesFile) {
        if (propertiesFile != null) {
            AgentProperties properties = PropertiesReader.readPropertiesFile(propertiesFile);
            profilerEnabled = properties.getPropertyAsBoolean("isProfilerEnabled");
        }
    }

    public void enableProfiler() {
        profilerEnabled = true;
    }

    public void disableProfiler() {
        profilerEnabled = false;
    }

    public boolean isProfilerEnabled() {
        return profilerEnabled;
    }

    public void addThread(long threadId) {
        threadsToProfile.add(threadId);
    }

    public void removeThread(long threadId) {
        threadsToProfile.remove(threadId);
    }

    public boolean isThreadEnabled(long threadId) {
        return threadsToProfile.contains(threadId);
    }

    public String getTraceFileString() {
        return TRC_FILE;
    }

}
