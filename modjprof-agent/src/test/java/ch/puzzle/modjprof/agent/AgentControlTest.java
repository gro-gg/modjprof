package ch.puzzle.modjprof.agent;

import java.lang.reflect.Method;

import org.junit.Test;

public class AgentControlTest {

    @Test
    public void shouldInvokeStartAgentWithoutException() throws Exception {
        //when
        invokeAgent("startAgent");

        //then
        //no Exception should be thrown
    }

    @Test
    public void shouldInvokeStopAgentWithoutException() throws Exception {
        //when
        invokeAgent("stopAgent");

        //then
        //no Exception should be thrown
    }

    @Test
    public void shouldInvokeListTraceFilesWithoutException() throws Exception {
        //when
        invokeAgent("listTraceFiles");

        //then
        //no Exception should be thrown
    }

    @Test(expected = NoSuchMethodException.class)
    public void shouldCatchNoSuchMethodExceptionWhenInvokingNonexistingMethod() throws Exception {
        //when
        invokeAgent("nonexistingMethod");

        //then
        //NoSuchMethodException should be thrown
    }

    /*
     * invoke the agent with reflection like the ControlServlet will do.
     */
    private void invokeAgent(String method) throws Exception {
        Class<?> c = Class.forName("ch.puzzle.modjprof.agent.AgentControl");
        Method getInstanceMethod = c.getMethod("getInstance");
        Object instance = getInstanceMethod.invoke(null);
        Method startAgentMethod = c.getMethod(method);
        startAgentMethod.invoke(instance);
    }
}
