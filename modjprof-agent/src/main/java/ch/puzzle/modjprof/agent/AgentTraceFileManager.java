package ch.puzzle.modjprof.agent;

import static ch.puzzle.modjprof.agent.AgentConfiguration.TRC_FILE_DIR;

import java.io.File;
import java.io.FileFilter;

/**
 * This class is responsible to return or delete all agent trace files.
 */
public class AgentTraceFileManager {

    protected void deleteAllTraceFiles() {
        File[] matchingFiles = findAllTraceFiles();
        for (int i = 0; i < matchingFiles.length; i++) {
            matchingFiles[i].delete();
        }
    }

    protected long[] getAllTraceFileThreadIds() {
        File[] traceFiles = findAllTraceFiles();
        long[] threadIds = new long[traceFiles.length];

        String[] fileNameParts = (new File(AgentConfiguration.getInstance().getTraceFileString())).getName().split("%d");
        for (int i = 0; i < traceFiles.length; i++) {
            String fileName = traceFiles[i].getName();
            String threadId = fileName.replaceFirst(fileNameParts[0], "").replaceAll(fileNameParts[1], "");
            threadIds[i] = Long.parseLong(threadId);
        }
        return threadIds;
    }

    protected File[] findAllTraceFiles() {
        File[] matchingFiles = (new File(TRC_FILE_DIR)).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //TODO use TRC_FILE
                return pathname.getName().startsWith("modjprof_") && pathname.getName().endsWith(".trc");
            }
        });
        return matchingFiles;
    }

}
