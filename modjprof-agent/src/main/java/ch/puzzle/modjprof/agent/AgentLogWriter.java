package ch.puzzle.modjprof;

import static ch.puzzle.modjprof.AgentConfiguration.TRC_FILE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AgentLogWriter {

    public void writeEnterMethod(String methodSignature) {
        writeMethodCallLine(">", methodSignature);
    }

    public void writeExitMethod() {
        writeMethodCallLine("<");
    }

    private void writeMethodCallLine(String flowPattern) {
        writeMethodCallLine(flowPattern, null);
    }

    private void writeMethodCallLine(String flowPattern, String methodSignature) {
        if (AgentConfiguration.isProfilerEnabled()) {
            long threadId = Thread.currentThread().getId();
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter(new File(String.format(TRC_FILE, threadId)), true), true);
                writer.println(buildMethodCallLineString(flowPattern, methodSignature));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                writer.close();
            }
        }
    }

    private String buildMethodCallLineString(String flowPattern, String methodSignature) {
        StringBuffer sb = new StringBuffer();
        sb.append(flowPattern);
        sb.append(" ").append(getMilliString());
        if (methodSignature != null) {
            sb.append(" ").append(methodSignature);
        }
        return sb.toString();
    }

    private String getMilliString() {
        long nanoTime = System.nanoTime();
        int pos = 6;
        String x = Long.toString(nanoTime);
        x = x.substring(0, x.length() - pos) + "." + x.substring(x.length() - pos, x.length());
        return x;
    }

}
