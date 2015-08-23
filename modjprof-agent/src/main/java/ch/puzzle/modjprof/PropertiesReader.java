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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class PropertiesReader {

    private final static Logger LOGGER = Logger.getLogger(PropertiesReader.class.getName());

    public static AgentProperties readPropertiesFile(String propertiesFile) {
        AgentProperties properties = new AgentProperties();
        InputStream input = null;
        try {
            input = new FileInputStream(propertiesFile);
            properties.load(input);
        } catch (FileNotFoundException e) {
            LOGGER.severe("Properties file " + propertiesFile + " not found!");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        return properties;
    }
}
