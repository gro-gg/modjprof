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

public class TestConstants {

    private static final String VERSION = "0.0.1-SNAPSHOT";

    public static final String AGENT_JAR = "modjprof-agent-" + VERSION + ".jar";

    public static final String APPLICATION_JAR = "sample-application-" + VERSION + "-jar-with-dependencies.jar";

    public static final String AGENT_JAR_LOCATION = "target/" + AGENT_JAR;

    public static final String APPLICATION_JAR_LOCATION = "../sample-application/target/" + APPLICATION_JAR;

}
