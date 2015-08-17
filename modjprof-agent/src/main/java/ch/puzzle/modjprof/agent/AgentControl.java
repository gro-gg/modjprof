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
        System.err.println("AgentControl: startAgent() called");
    }

    public void stopAgent() {
        AgentConfiguration.disableProfiler();
        System.err.println("AgentControl: stopAgent() called");
    }

    public void listTraceFiles() {

    }
}
