package ch.puzzle.modjprof;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AgentProperties extends Properties {

    private static final long serialVersionUID = 4663434891809094508L;

    public Set<String> getPropertyAsSet(String key) {
        String[] split = getProperty(key).split(",");
        return new HashSet<String>(Arrays.asList(split));
    }

    public boolean getPropertyAsBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

}
