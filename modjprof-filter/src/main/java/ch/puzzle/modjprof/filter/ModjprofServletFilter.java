package ch.puzzle.modjprof.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;


@WebFilter("/*")
public class ModjprofServletFilter implements Filter {

    private final static Logger LOGGER = Logger.getLogger(ModjprofServletFilter.class.getName());

    @Override
    public void destroy() {
        LOGGER.warning(ModjprofServletFilter.class.getName() + " destroyed");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOGGER.warning("doFilter()");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.warning("init() " + filterConfig.getFilterName());
    }

}
