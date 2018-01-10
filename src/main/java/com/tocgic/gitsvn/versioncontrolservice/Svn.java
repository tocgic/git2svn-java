package com.tocgic.gitsvn.versioncontrolservice;

import com.tocgic.gitsvn.util.Out;

public class Svn extends Vcs {
    public Svn(String remoteUrl, String repoDirectory, String authUser, String authPass) {
        super(remoteUrl, repoDirectory, authUser, authPass);
    }
    
    @Override
    protected String getVcsName() {
        return "svn";
    }

    @Override
    protected String getOptionNameUser() {
        return "--username";
    }

    @Override
    protected String getOptionNamePass() {
        return "--password";
    }

    /**
     * svn checkout --force ${SVN_URL} ${SVN_DIR}
     */
    public String checkout() {
        Out.println(Out.ANSI_GREEN, "... svn checkout --force "+remoteUrl+" "+repoDirectory);
        return run(makeParam("checkout", "--force", remoteUrl, repoDirectory));
    }

    /**
     * svn revert {repoDirectory} -R
     */
    public String revert() {
        Out.println(Out.ANSI_GREEN, "... svn revert -R");
        return run(makeParam("revert", repoDirectory, "-R"));
    }

    /**
     * svn cleanup --remove-unversioned
     */
    public String cleanup() {
        Out.println(Out.ANSI_GREEN, "... svn cleanup --remove-unversioned");
        return run(makeParam("cleanup", repoDirectory, "--remove-unversioned"));
    }

    /**
     * svn update
     */
    public String update() {
        Out.println(Out.ANSI_GREEN, "... svn update");
        return run(makeParam("update", repoDirectory));
    }

    /**
     * svn log --xml -l 1
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <log>
     * <logentry
     *    revision="17417">
     * <author>jycho</author>
     * <date>2018-01-08T09:56:29.737376Z</date>
     * <msg>2017/05/02 08:50:49 [djpark0402]: OneGuard 프래임워크 프로젝트 업로드
     * 
     * GitCommitHash:4e08059f7fa1965cd9d2f410ed998a489fd14ef1</msg>
     * </logentry>
     * </log>
     */
    public String getLastXmlLog() {
        Out.println(Out.ANSI_GREEN, "... svn log --xml -l 1");
        return run(makeParam("log", repoDirectory, "--xml", "-l", "1"));
    }

    /**
     * svn status {repoDirectory}
     */
    public String status() {
        Out.println(Out.ANSI_GREEN, "... svn status");
        return run(makeParam("status", repoDirectory));
    }

    /**
     * svn commit -m {message}
     */
    public String commit(String commitMessage) {
        Out.println(Out.ANSI_GREEN, "... svn commit -m "+commitMessage);
        return run(makeParam("commit", repoDirectory, "-m", commitMessage));
    }

}