package com.tocgic.gitsvn.versioncontrolservice;

import com.tocgic.gitsvn.util.Out;
import com.tocgic.gitsvn.util.RuntimeExecutor;
import java.io.File;
import java.util.ArrayList;

abstract public class Vcs {
    protected String remoteUrl;
    protected String repoDirectory;
    protected String authUser;
    protected String authPass;
    private RuntimeExecutor executor;
    boolean isDebug;

    abstract String getVcsName();
    abstract String getOptionNameUser();
    abstract String getOptionNamePass();

    public Vcs(String remoteUrl, String repoDirectory, String authUser, String authPass) {
        this.remoteUrl = remoteUrl;
        this.repoDirectory = repoDirectory;
        this.authUser = authUser;
        this.authPass = authPass;
        executor = new RuntimeExecutor(isDebug);
        if (this.repoDirectory != null && this.repoDirectory.length() > 0) {
            if (this.repoDirectory.endsWith(File.separator)) {
                this.repoDirectory.substring(0, this.repoDirectory.length()-1);
            }
            File svnDirectory = new File(repoDirectory);
            if (!svnDirectory.exists()) {
                svnDirectory.mkdirs();
            }
            executor.setWorkingDirectory(repoDirectory);
        }
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        if (executor != null) {
            executor.setDebug(isDebug);
        }
    }

    public String getRepoDirectory() {
        return repoDirectory;
    }

    protected ArrayList<String> makeParam() {
        ArrayList<String> list = new ArrayList<>();
        list.add(getVcsName());
        if (authUser != null && authUser.length() > 0) {
            list.add(getOptionNameUser());
            list.add(authUser);
        }
        if (authPass != null && authPass.length() > 0) {
            list.add(getOptionNamePass());
            list.add(authPass);
        }
        return list;
    }

    protected String[] makeParam(String... commands) {
        ArrayList<String> list = makeParam();
        if (commands != null) {
            for (String command : commands) {
                list.add(command);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public String run(String... commands) {
        String result = null;
        result = executor.execAndRtnResult(commands);
        //Out.println(result);

        // try {
        //     result = executor.execAndRtnResult(commands);
        //     Out.println(result);
        // } catch (Exception e) {
        //     result = e.getMessage();
        //     Out.println(Out.ANSI_RED, result);
        // }
        return result;
    }
}
