package ch.puzzle.modjprof.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;


@WebFilter("/*")
public class ModjprofServletFilter implements Filter {

    private final static Logger LOGGER = Logger.getLogger(ModjprofServletFilter.class.getName());

    @Override
    public void destroy() {
        LOGGER.warning(ModjprofServletFilter.class.getName() + " destroyed");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String userAgent = httpRequest.getHeader("User-Agent");

        long threadId = Thread.currentThread().getId();
        if (userAgent.contains("modjprof")) {
            LOGGER.warning("Start profiling for current thread " + threadId);
            invokeAgent("enableThread", threadId);
        }

        chain.doFilter(request, response);

        if (userAgent.contains("modjprof")) {
            invokeAgent("disableThread", threadId);
            LOGGER.warning("Stop profiling for current thread " + threadId);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.warning("init() " + filterConfig.getFilterName());
    }

    private void invokeAgent(String method, long threadId) {
        try {
            Class<?> c = Class.forName("ch.puzzle.modjprof.agent.AgentControl");
            Method getInstanceMethod = c.getMethod("getInstance");
            Object instance = getInstanceMethod.invoke(null);
            Method startAgentMethod = c.getMethod(method, long.class);
            startAgentMethod.invoke(instance, threadId);
        } catch (Exception e) {
            LOGGER.severe("Cannot invoke Agent! " + e.getMessage());
            e.printStackTrace();
        }
    }

}
