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
