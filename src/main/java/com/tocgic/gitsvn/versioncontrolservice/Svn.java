package com.tocgic.gitsvn.versioncontrolservice;

import com.tocgic.gitsvn.util.Out;
import com.tocgic.gitsvn.util.RuntimeExecutor;

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
     * svn checkout ${SVN_URL} ${SVN_DIR}
     *
     * @param isForce : --force option
     */
    public String checkout(boolean isForce) {
        if (remoteUrl == null || remoteUrl.length() < 1) {
            return null;
        }
        Out.println(Out.ANSI_GREEN, "... svn.checkout("+isForce+")");
        if (isForce) {
            return run(makeParam("checkout", "--force", remoteUrl, "."));
        } else {
            return run(makeParam("checkout", remoteUrl, "."));
        }
    }

    /**
     * svn revert -R
     */
    public String revert() {
        Out.println(Out.ANSI_GREEN, "... svn.revert()");
        return run(makeParam("revert", ".", "-R"));
    }

    /**
     * svn cleanup --remove-unversioned
     */
    public String cleanup() {
        Out.println(Out.ANSI_GREEN, "... svn.cleanup()");
        return run(makeParam("cleanup", "--remove-unversioned"));
    }

    /**
     * svn update
     */
    public String update() {
        Out.println(Out.ANSI_GREEN, "... svn.update()");
        return run(makeParam("update"));
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
        Out.println(Out.ANSI_GREEN, "... svn.getLastXmlLog()");
        return run(makeParam("log", "--xml", "-l", "1"));
    }

    /**
     * svn status
     */
    public String status() {
        Out.println(Out.ANSI_GREEN, "... svn.status()");
        return run(makeParam("status", repoDirectory));
    }

    /**
     * svn add
     */
    public String add(String fileName) {
        Out.println(Out.ANSI_GREEN, "... svn.add("+fileName+")");
        int tryLimit = 2;
        if (fileName != null && fileName.length() > 0) {
            if (fileName.contains("@")) {
                fileName += "@";
            }
            do {
                tryLimit--;     
                String result = run(makeParam("add", "--force", "\""+fileName+"\""));
                // String result = return run(makeParam("add", fileName));
                if (!RuntimeExecutor.isErrorResponse(result)) {
                    return result;
                }
            } while (tryLimit > 0);
        }
        return null;
    }

    /**
     * svn rm
     */
    public String rm(String fileName) {
        Out.println(Out.ANSI_GREEN, "... svn.rm("+fileName+")");
        if (fileName != null && fileName.length() > 0) {
            if (fileName.contains("@")) {
                fileName += "@";
            }
            return run(makeParam("rm", "\""+fileName+"\""));
        }
        return null;
    }

    /**
     * svn commit -m {message}
     */
    public String commit(String commitMessage) {
        String messageHead = (commitMessage != null && commitMessage.length() > 50) ? commitMessage.substring(0, 50) + "..." : commitMessage;
        Out.println(Out.ANSI_GREEN, "... svn.commit("+messageHead+")");
        return run(makeParam("commit", "-m", commitMessage));
    }
}