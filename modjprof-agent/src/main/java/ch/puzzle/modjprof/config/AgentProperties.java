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
package ch.puzzle.modjprof.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AgentProperties extends Properties {

    private static final String PROPERTY_DELIMITER = ",";

    private static final long serialVersionUID = 4663434891809094508L;

    public Set<String> getPropertyAsSet(String key) {
        String property = getProperty(key);
        if (property != null) {
            String[] split = property.split(PROPERTY_DELIMITER);
            return new HashSet<String>(Arrays.asList(split));
        }
        return new HashSet<String>();
    }

    public boolean getPropertyAsBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public static AgentProperties parsePropertiesString(String propertiesString) throws IOException {
        AgentProperties properties = new AgentProperties();
        if (propertiesString != null) {
            properties.load(new StringReader(propertiesString));
        }
        return properties;
    }
}
