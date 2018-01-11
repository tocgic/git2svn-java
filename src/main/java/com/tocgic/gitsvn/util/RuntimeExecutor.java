package com.tocgic.gitsvn.util;

import com.tocgic.gitsvn.util.Out;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class RuntimeExecutor {
    private File workingDirectory;

    public RuntimeExecutor() {

    }

    public void setWorkingDirectory(String directory) {
        if (directory != null && directory.length() > 0) {
            workingDirectory = new File(directory);
            if (!workingDirectory.exists()) {
                workingDirectory.mkdirs();
            }
        } else {
            workingDirectory = null;
        }
    }

    public void byCommonsExec(String[] command) throws IOException, InterruptedException {
        CommandLine cmdLine = CommandLine.parse(command[0]);
        for (int i=1, n=command.length ; i<n ; i++ ) {
            if (command[i] != null && command[i].length() > 0) {
                cmdLine.addArgument(command[i]);
            }
        }
        DefaultExecutor executor = new DefaultExecutor();
        if (workingDirectory != null) {
            executor.setWorkingDirectory(workingDirectory);
            //Out.println(Out.ANSI_PURPLE, "... workingDirectory : "+workingDirectory.getAbsolutePath());
        }
        // executor.getStreamHandler();
        executor.execute(cmdLine);
    }

    /**
     * 외부 명령 실행 후 결과를 String으로 받음
     * @param command
     * @return
     * @throws Exception
     */
    public String execAndRtnResult(String[] command) throws Exception {
        String rtnStr = "";
        CommandLine cmdLine = CommandLine.parse(command[0]);
        for (int i=1, n=command.length ; i<n ; i++ ) {
            if (command[i] != null && command[i].length() > 0) {
                cmdLine.addArgument(command[i]);
            }
        }
        DefaultExecutor executor = new DefaultExecutor();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(baos);
        executor.setStreamHandler(streamHandler);
        if (workingDirectory != null) {
            executor.setWorkingDirectory(workingDirectory);
            //Out.println(Out.ANSI_PURPLE, "... workingDirectory : "+workingDirectory.getAbsolutePath());
        }
        try {
            String params = "";
            for (String cmd : command) {
                params += cmd + " ";
            }
            Out.println(Out.ANSI_PURPLE, "... runtime$ " + params);
            
            int exitCode = executor.execute(cmdLine);
            rtnStr = baos.toString();

            Out.println(Out.ANSI_PURPLE, "... runtime: " + exitCode);
            //Out.println(Out.ANSI_YELLOW, "outputStr : " + rtnStr);
        } catch (Exception e) {
            Out.println(Out.ANSI_RED, "error : " + e.getMessage());
            throw new Exception(e.getMessage(), e);
        }
        return rtnStr;

    }
}