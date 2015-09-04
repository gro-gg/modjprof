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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import ch.puzzle.modjprof.config.AgentProperties;
import ch.puzzle.modjprof.config.PropertiesReader;

public class InstrumentationConfiguration {

    private final static Logger LOGGER = Logger.getLogger(InstrumentationConfiguration.class.getName());

    static {
        LOGGER.info("class loaded by " + InstrumentationConfiguration.class.getClassLoader());
    }

    /*
     * eager thread safe singleton, only visible in this package
     */
    private static final InstrumentationConfiguration INSTANCE = new InstrumentationConfiguration();

    private InstrumentationConfiguration() {
    }

    static InstrumentationConfiguration getInstance() {
        return INSTANCE;
    }


    private Set<String> packagesToInstrument = new HashSet<String>();

    void initialize(String propertiesFile) {
        if (propertiesFile != null) {
            AgentProperties properties = PropertiesReader.readPropertiesFile(propertiesFile);
            packagesToInstrument = properties.getPropertyAsSet("packagesToInstrument");
        }
    }

    public Set<String> getPackagesToInstrument() {
        return packagesToInstrument;
    }

}
