/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */
package ch.puzzle.modjprof.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;

@WebServlet(name = "ControlServlet", urlPatterns = { "/*" })
public class ControlServlet extends HttpServlet {

    private static final long serialVersionUID = 7289197726261711665L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        try {
            printResponseHeader(out, pathInfo);
            evaluateCommandAndExecuteIt(pathInfo, getBaseUri(request), out);
            printResponseFooter(out);
        } finally {
            out.close();
        }
    }

    private String getBaseUri(HttpServletRequest request) {
        String baseURI = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath() + "/";
        return baseURI;
    }

    private void evaluateCommandAndExecuteIt(String pathInfo, String baseURI, PrintWriter out) {
        if ("/".equals(pathInfo)) {
            printUsage(out, baseURI, false);
        } else if ("/start".equals(pathInfo)) {
            startProfiler(out);
        } else if ("/stop".equals(pathInfo)) {
            stopProfiler(out);
        } else if ("/list".equals(pathInfo)) {
            listFiles(out, baseURI);
        } else {
            printUsage(out, baseURI, true);
        }
    }

    private void startProfiler(PrintWriter out) {
        invokeAgent("startAgent", out);
        out.println("<p>Profiler started!</p>");
    }

    private void stopProfiler(PrintWriter out) {
        invokeAgent("stopAgent", out);
        out.println("<p>Profiler stopped!</p>");
    }

    private void listFiles(PrintWriter out, String baseURI) {
        File[] files = (File[]) invokeAgent("listTraceFiles", out);
        if (files.length == 0) {
            out.println("<p>No files found!</p>");
            return;
        }
        out.println("<p>Found the following trace files:</p>");
        out.println("<ul>");
        for (int i = 0; i < files.length; i++) {
            try {
                out.println("<li>");
                String taceFile = files[i].getCanonicalPath();
                String downloadUrl = baseURI + "downloadfile?file=" + taceFile;
                out.println("<a href=\"" + downloadUrl + "\"  target=\"_blank\">" + files[i].getName() + "</a>");
                out.println("</li>");
            } catch (IOException e) {
                e.printStackTrace(out);
            }
        }
        out.println("</ul>");
    }

    private Object invokeAgent(String method, PrintWriter out) {
        try {
            Class<?> c = Class.forName("ch.puzzle.modjprof.agent.AgentControl");
            Method getInstanceMethod = c.getMethod("getInstance");
            Object instance = getInstanceMethod.invoke(null);
            Method agentMethod = c.getMethod(method);
            return agentMethod.invoke(instance);
        } catch (ClassNotFoundException e) {
            printError(e, out);
        } catch (NoSuchMethodException e) {
            printError(e, out);
        } catch (SecurityException e) {
            printError(e, out);
        } catch (IllegalAccessException e) {
            printError(e, out);
        } catch (IllegalArgumentException e) {
            printError(e, out);
        } catch (InvocationTargetException e) {
            printError(e, out);
        }
        return null;
    }

    private void printError(Exception e, PrintWriter out) {
        out.println("<p><font color=\"red\">");
        out.println(ExceptionUtils.getStackTrace(e));
        out.println("</font></p>");
        e.printStackTrace();
    }

    private void printUsage(PrintWriter out, String baseURI, boolean unknownCommand) {
        if (unknownCommand) {
            out.println("<p><font color=\"red\">unknown command!</font></p>");
        }
        out.println("Usage:");
        out.println("<table border=\"0\"><col width=\"130\">");
        out.println("<tr><td><a href=\"" + baseURI + "start\">/start</a></td><td>will start the profiler</td></tr>");
        out.println("<tr><td><a href=\"" + baseURI + "stop\">/stop</a></td><td>will stop the profiler</td></tr>");
        out.println("<tr><td><a href=\"" + baseURI + "list\">/list</a></td><td>will list all trace files</td></tr>");
        out.println("</table>");
    }

    private void printResponseHeader(PrintWriter out, String pathInfo) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>modjprof ControlServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>modjprof ControlServlet</h1>");
        //out.println("<p>PathInfo: " + pathInfo + "</p>");
    }

    private void printResponseFooter(PrintWriter out) {
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to control modjprof java agent";
    }

}
