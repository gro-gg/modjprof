package ch.puzzle.modjprof.agent;

import java.util.logging.Logger;

public class AgentControl {

    private static final AgentControl instance;

    private final static Logger LOGGER = Logger.getLogger(AgentControl.class.getName());

    static {
        instance = new AgentControl();
    }

    public static AgentControl getInstance() {
        return instance;
    }

    public void startAgent() {
        AgentConfiguration.enableProfiler();
        LOGGER.info("startAgent() called");
    }

    public void stopAgent() {
        AgentConfiguration.disableProfiler();
        LOGGER.info("stopAgent() called");
    }

    public void listTraceFiles() {

    }
}
