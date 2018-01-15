package com.tocgic.gitsvn.versioncontrolservice;

import com.tocgic.gitsvn.util.Out;

public class Git extends Vcs {
    private String branchName;

    public Git(String remoteUrl, String repoDirectory, String authUser, String authPass) {
        super(remoteUrl, repoDirectory, authUser, authPass);
    }

    @Override
    protected String getVcsName() {
        return "git";
    }

    @Override
    protected String getOptionNameUser() {
        return null;
    }

    @Override
    protected String getOptionNamePass() {
        return null;
    }

    /**
     * git clone https://username:password@github.com/username/repository.git
     */
    public String clone() {
        if (remoteUrl == null || remoteUrl.length() < 1) {
            return null;
        }
        Out.println(Out.ANSI_GREEN, "... git.clone()");
        //TODO : remoteAuthUrl (add username & password)
        String remoteAuthUrl = this.remoteUrl;
        return run(makeParam("clone", remoteAuthUrl, "."));
    }

    /**
     * git checkout {branchName}
     * 
     * @param branchOrCommit : branch or commit
     * @param isForce : --force option
     */
    public String checkout(String branchOrCommit, boolean isForce) {
        Out.println(Out.ANSI_GREEN, "... git.checkout("+branchOrCommit+","+isForce+")");
        this.branchName = branchOrCommit;
        if (isForce) {
            return run(makeParam("checkout", branchOrCommit, "--force"));
        } else {
            return run(makeParam("checkout", branchOrCommit));
        }
    }

    /**
     * git rev-list $GIT_BRANCH_NAME --all-match --reverse
     * 
     * @param branchName : branchName
     */
    public String getRevListAllMatch(String branchName) {
        if (branchName == null || branchName.length() < 1) {
            branchName = "master";
        }
        Out.println(Out.ANSI_GREEN, "... git.getRevListAllMatch("+branchName+")");
        return run(makeParam("rev-list", branchName, "--all-match", "--reverse"));
    }

    /**
     * git log -n 1 --date=format:"%Y/%m/%d %H:%M:%S" --pretty=format:{prettyFormatOptions} ${commit}
     * 
     * @param commit : commitHashValue (SHA-1)
     * @param prettyFormatOptions : %an (author), %s (msg), %cd (date {YYYY/MM/DD HH:MM:SS})
     */
    public String getLogValue(String commit, String prettyFormatOptions) {
        if (isDebug) {
            Out.println(Out.ANSI_GREEN, "... git.getLogValue("+commit+","+prettyFormatOptions+")");
        }
        return run(makeParam("log", "-n", "1", "--date=format:%Y/%m/%d_%H:%M:%S", "--pretty=format:"+prettyFormatOptions, commit));
    }

    public String getLogValueAuthor(String commit) {
        return getLogValue(commit, "%an");
    }

    public String getLogValueSubject(String commit) {
        return getLogValue(commit, "%s");
    }

    public String getLogValueMsg(String commit) {
        return getLogValue(commit, "%b");
    }

    public String getLogValueDate(String commit) {
        return getLogValue(commit, "%cd");
    }
}