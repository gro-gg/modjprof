package ch.puzzle.modjprof;

public class AgentControl {

    private static final AgentControl instance;

    static {
        instance = new AgentControl();
    }

    public static AgentControl getInstance() {
        return instance;
    }

    public void startAgent() {
        AgentConfiguration.enableProfiler();
    }

    public void stopAgent() {
        AgentConfiguration.disableProfiler();
    }
}
