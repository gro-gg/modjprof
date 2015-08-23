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
}
