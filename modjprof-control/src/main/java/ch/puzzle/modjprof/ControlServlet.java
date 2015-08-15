package ch.puzzle.modjprof;

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
        String pathInfo = request.getPathInfo();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            printResponseHeader(out, pathInfo);
            if ("/".equals(pathInfo)) {
                printUsage(out, false);
            } else if ("/start".equals(pathInfo)) {
                startProfiler(out, pathInfo);
            } else if ("/stop".equals(pathInfo)) {
                stopProfiler(out, pathInfo);
            } else {
                printUsage(out, true);
            }
            printResponseFooter(out);
        } finally {
            out.close();
        }
    }

    private void startProfiler(PrintWriter out, String pathInfo) {
        invokeAgent("startAgent", out);
        out.println("<p>Profiler started!</p>");
    }

    private void stopProfiler(PrintWriter out, String pathInfo) {
        invokeAgent("stopAgent", out);
        out.println("<p>Profiler stopped!</p>");
    }

    private void invokeAgent(String method, PrintWriter out) {
        try {
            Class<?> c = Class.forName("ch.puzzle.modjprof.AgentControl");
            Method getInstanceMethod = c.getMethod("getInstance");
            Object instance = getInstanceMethod.invoke(null);
            Method startAgentMethod = c.getMethod(method);
            startAgentMethod.invoke(instance);
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
    }

    private void printError(Exception e, PrintWriter out) {
        out.println("<p><font color=\"red\">");
        out.println(ExceptionUtils.getStackTrace(e));
        out.println("</font></p>");
        e.printStackTrace();
    }

    private void printUsage(PrintWriter out, boolean unknownCommand) {
        if (unknownCommand) {
            out.println("<p><font color=\"red\">unknown command!</font></p>");
        }
        out.println("Usage:");
        out.println("<table border=\"0\"><col width=\"130\">");
        out.println("<tr><td><a href=\"start\">/start</a></td><td>will start the profiler</td></tr>");
        out.println("<tr><td><a href=\"stop\">/stop</a></td><td>will stop the profiler</td></tr>");
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
        return "Short description";
    }

}
