package ch.puzzle.modjprof.instrumentation;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import ch.puzzle.modjprof.AgentProperties;
import ch.puzzle.modjprof.PropertiesReader;

public class InstrumentationConfiguration {


    private final static Logger LOGGER = Logger.getLogger(InstrumentationConfiguration.class.getName());
    static {
        LOGGER.info("class loaded by " + InstrumentationConfiguration.class.getClassLoader());
    }

    /**
     * thread safe singleton
     */
    private static final InstrumentationConfiguration INSTANCE = new InstrumentationConfiguration();
    private InstrumentationConfiguration() {
    }
    public static InstrumentationConfiguration getInstance() {
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
