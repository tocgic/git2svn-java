package com.tocgic.gitsvn.versioncontrolservice;

import com.tocgic.gitsvn.util.Out;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class RuntimeExecutor {
    public RuntimeExecutor() {

    }

    public void byCommonsExec(String[] command) throws IOException, InterruptedException {
        CommandLine cmdLine = CommandLine.parse(command[0]);
        for (int i=1, n=command.length ; i<n ; i++ ) {
            if (command[i] != null && command[i].length() > 0) {
                cmdLine.addArgument(command[i]);
            }
        }
        DefaultExecutor executor = new DefaultExecutor();
        executor.getStreamHandler();
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

        try {

            int exitCode = executor.execute(cmdLine);
            rtnStr = baos.toString();

            Out.println(Out.ANSI_GREEN, "... exitCode : " + exitCode);
            //Out.println(Out.ANSI_YELLOW, "outputStr : " + rtnStr);

        } catch (Exception e) {
            Out.println(Out.ANSI_RED, "error : " + e.getMessage());
            throw new Exception(e.getMessage(), e);
        }
        return rtnStr;

    }
}