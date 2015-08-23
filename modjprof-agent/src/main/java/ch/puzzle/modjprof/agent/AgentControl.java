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

import java.util.logging.Logger;

public class AgentControl {

    private final static Logger LOGGER = Logger.getLogger(AgentControl.class.getName());

    /**
     * lazy thread safe singleton
     */
    private static final class LazyHolder {
        private static final AgentControl INSTANCE = new AgentControl();
    }
    private AgentControl() {
    }
    public static AgentControl getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void startAgent() {
        AgentConfiguration.getInstance().enableProfiler();
        LOGGER.info("startAgent() called");
    }

    public void stopAgent() {
        AgentConfiguration.getInstance().disableProfiler();
        LOGGER.info("stopAgent() called");
    }

    public void listTraceFiles() {

    }

    public void enableThread(long threadId) {
        AgentConfiguration.getInstance().addThread(threadId);
        LOGGER.info("enableThread(" + threadId + ") called");
    }

    public void disableThread(long threadId) {
        AgentConfiguration.getInstance().removeThread(threadId);
        LOGGER.info("disableThread(" + threadId + ") called");
    }
}
