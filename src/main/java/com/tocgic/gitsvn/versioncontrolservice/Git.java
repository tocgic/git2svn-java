package com.tocgic.gitsvn.versioncontrolservice;

import java.net.URI;
import java.net.URLEncoder;

import com.tocgic.gitsvn.util.Out;

public class Git extends Vcs {
    private String branchName;

    public Git(String remoteUrl, String repoDirectory, String authUser, String authPass) {
        super(remoteUrl, repoDirectory, authUser, authPass);
        checkAuthenticationInformation();
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
     * git Url 인증 정보 확인
     * git@ 이 아닌 https 의 프로토콜은 사용자 인증정보를 포함시킨다.
     * https://username:password@github.com/username/repository.git
     */
    private void checkAuthenticationInformation() {
        if (authUser == null || authUser.length() < 1) {
            return;
        }
        if (authPass == null || authPass.length() < 1) {
            return;
        }
        if (remoteUrl == null || remoteUrl.length() < 1) {
            return;
        }
        if (remoteUrl.toLowerCase().startsWith("git@")) {
            return;
        }
        URI uri = URI.create(remoteUrl);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        // Out.println("scheme:"+scheme);
        // Out.println("authUser:"+authUser);
        // Out.println("authPass:"+authPass);
        // Out.println("host:"+host);
        // Out.println("port:"+port);
        // Out.println("path:"+path);
        remoteUrl = scheme + "://" + authUser + ":" + urlEncode(authPass) + "@" + host + ((port > 0) ? (":"+port) : "") + path;
        // Out.println("remoteUrl:"+remoteUrl);
        authUser = null;
        authPass = null;
        Out.println(Out.ANSI_GREEN, "checkAuthenticationInformation(), remoteUrl updated");
    }

    /**
     * https://fabianlee.org/2016/09/07/git-calling-git-clone-using-password-with-special-character/
     * 
     * !   #   $    &   '   (   )   *   +   ,   /   :   ;   =   ?   @   [   ]
     * %21 %23 %24 %26 %27 %28 %29 %2A %2B %2C %2F %3A %3B %3D %3F %40 %5B %5D
     */
    private String urlEncode(final String source) {
        String password = source;
        if (password == null || password.length() < 1) {
            return password;
        }
        try {
            password = URLEncoder.encode(password, "UTF-8");
        } catch (Exception e) {}
        return password;
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
     * git pull https://username:password@github.com/username/repository.git master
     */
    public String pull(String branchName) {
        if (remoteUrl == null || remoteUrl.length() < 1) {
            return null;
        }
        if (branchName == null || branchName.length() < 1) {
            return null;
        }
        Out.println(Out.ANSI_GREEN, "... git.pull("+branchName+")");
        //TODO : remoteAuthUrl (add username & password)
        String remoteAuthUrl = this.remoteUrl;
        return run(makeParam("pull", remoteAuthUrl, branchName));
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

    public String getLogValueSubjectAndMsg(String commit) {
        return getLogValue(commit, "%B");
    }

    public String getLogValueDate(String commit) {
        return getLogValue(commit, "%cd");
    }
}