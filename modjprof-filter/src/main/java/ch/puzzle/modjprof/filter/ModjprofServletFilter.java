/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */
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
