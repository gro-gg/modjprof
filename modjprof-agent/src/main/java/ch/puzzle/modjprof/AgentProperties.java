package ch.puzzle.modjprof;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AgentProperties extends Properties {

    private static final long serialVersionUID = 4663434891809094508L;

    public Set<String> getPropertyAsSet(String key) {
        String property = getProperty(key);
        if (property != null) {
            String[] split = property.split(",");
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
