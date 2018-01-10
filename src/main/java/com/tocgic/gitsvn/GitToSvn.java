package com.tocgic.gitsvn;

import com.tocgic.gitsvn.util.Out;
import com.tocgic.gitsvn.versioncontrolservice.Git;
import com.tocgic.gitsvn.versioncontrolservice.Svn;

public class GitToSvn {
    private Svn svn;
    private Git git;

    public static void main(String args[]) {
        if (args == null || args.length < 2) {
            Out.println(Out.ANSI_YELLOW, "usage: $0 [gitDir] $1 [svnDir]");
            return;
        }
        // String gitDir = args[0];
        // String svnDir = args[1];
        String svnUrl = "https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK";
        String svnDir = "/Users/tocgic/SvnRepository/test-svn-git/svnOneGuard";
        String svnUser = null;
        String svnPass = null;
        String gitUrl = "git@10.0.0.69:devone/TouchEn_OneGuard_4_0_ios_SDK.git";
        String gitDir = "/Users/tocgic/SvnRepository/test-svn-git/gitOneGuard";
        String gitUser = null;
        String gitPass = null;
        GitToSvn git2svn = new GitToSvn(svnUrl, svnDir, svnUser, svnPass, gitUrl, gitDir, gitUser, gitPass);
        git2svn.start();
    }

    public GitToSvn(String svnUrl, String svnDir, String svnUser, String svnPass, String gitUrl, String gitDir, String gitUser, String gitPass) {
        svn = new Svn(svnUrl, svnDir, svnUser, svnPass);
        git = new Git(gitUrl, gitDir, gitUser, gitPass);
    }

    public void start() {
        // svn.checkout();
        // svn.getLastXmlLog();
        git.checkout("master");
    }

}