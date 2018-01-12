package com.tocgic.gitsvn.util;

import com.tocgic.gitsvn.util.Out;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

public class RuntimeExecutor {
    public static final String RUNTIME_EXECUTOR_ERROR = "[RuntimeExecutor_error]";
    private File workingDirectory;
    private boolean isDebug;

    public RuntimeExecutor() {
    }

    public RuntimeExecutor(boolean isDebug) {
        setDebug(isDebug);
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
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
            if (isDebug) {
                Out.println(Out.ANSI_BLUE, "... workingDirectory : "+workingDirectory.getAbsolutePath());
            }
        }
        executor.execute(cmdLine);
    }

    /**
     * 외부 명령 실행 후 결과를 String으로 받음
     * @param command
     * @return
     * @throws Exception
     */
    public String execAndRtnResult(String[] command) {
        return execAndRtnResult(command, true);
    }

    public String execAndRtnResult(String[] command, boolean handleQuoting) {
        String rtnStr = "";
        CommandLine cmdLine = CommandLine.parse(command[0]);
        for (int i=1, n=command.length ; i<n ; i++ ) {
            if (command[i] != null && command[i].length() > 0) {
                // String item = command[i].replace(" ", "\\ ");
                // cmdLine.addArgument(item);
                cmdLine.addArgument(command[i], handleQuoting);
            }
        }
        DefaultExecutor executor = new DefaultExecutor();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(baos);
        executor.setStreamHandler(streamHandler);
        if (workingDirectory != null) {
            executor.setWorkingDirectory(workingDirectory);
            if (isDebug) {
                Out.println(Out.ANSI_BLUE, "... workingDirectory : "+workingDirectory.getAbsolutePath());
            }
        }
        try {
            String params = "";
            for (String cmd : command) {
                params += cmd + " ";
            }
            if (isDebug) {
                Out.println(Out.ANSI_BLUE, "... runtime$ " + params);
            }
            
            int exitCode = executor.execute(cmdLine);
            rtnStr = baos.toString();
        } catch (ExecuteException ee) {
            rtnStr += "\n\n"+RUNTIME_EXECUTOR_ERROR+" "+ee.getMessage();
            ee.printStackTrace();
        } catch (IOException ie) {
            rtnStr += "\n\n"+RUNTIME_EXECUTOR_ERROR+" "+ie.getMessage();
            ie.printStackTrace();
        }
        return rtnStr;
    }

    public static boolean isErrorResponse(String response) {
        return (response != null && response.contains(RUNTIME_EXECUTOR_ERROR));
    }
}