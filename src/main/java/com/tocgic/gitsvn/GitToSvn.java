package com.tocgic.gitsvn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import com.tocgic.gitsvn.util.Out;
import com.tocgic.gitsvn.util.RuntimeExecutor;
import com.tocgic.gitsvn.versioncontrolservice.Git;
import com.tocgic.gitsvn.versioncontrolservice.Svn;

public class GitToSvn {
    private static final String SVN_COMMIT_TAG = "GitCommitHash:";
    private Svn svn;
    private Git git;
    private RuntimeExecutor executor;
    private String sourceGitBranchName = "master";
    
    public static void main(String args[]) {
        String svnUrl = null;
        String svnDir = null;
        String svnUser = null;
        String svnPass = null;
        String gitUrl = null;
        String gitDir = null;
        String gitUser = null;
        String gitPass = null;

        if (args != null && args.length == 2) {
            gitDir = args[0];
            svnDir = args[1];
        } else if (args != null && args.length == 4) {
            gitDir = args[0];
            gitUrl = args[1];
            svnDir = args[2];
            svnUrl = args[3];
        } else if (args != null && args.length == 5) {
            gitDir = args[0];
            svnDir = args[1];
            svnUrl = args[2];
            svnUser = args[3];
            svnPass = args[4];
        } else if (args != null && args.length == 6) {
            gitDir = args[0];
            gitUrl = args[1];
            svnDir = args[2];
            svnUrl = args[3];
            svnUser = args[4];
            svnPass = args[5];
        } else if (args != null && args.length == 8) {
            gitDir = args[0];
            gitUrl = args[1];
            gitUser = args[2];
            gitPass = args[3];
            svnDir = args[4];
            svnUrl = args[5];
            svnUser = args[6];
            svnPass = args[7];
        } else {
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [gitDir] [svnDir]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"/Users/tocgic/tempTargetSvn\"");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [gitDir] [gitUrl] [svnDir] [svnUrl]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"git@10.0.0.69:devone/TouchEn_OneGuard_4_0_ios_SDK.git\" \"/Users/tocgic/temp/sourceSvn\" \"https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK\"");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [gitDir] [svnDir] [svnUrl] [svnUser] [svnPass]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"/Users/tocgic/temp/sourceSvn\" \"https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK\" svnUserId svnPassword");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [gitDir] [gitUrl] [svnDir] [svnUrl] [svnUser] [svnPass]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"git@10.0.0.69:devone/TouchEn_OneGuard_4_0_ios_SDK.git\" \"/Users/tocgic/temp/sourceSvn\" \"https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK\" svnUserId svnPassword");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [gitDir] [gitUrl] [gitUser] [gitPass] [svnDir] [svnUrl] [svnUser] [svnPass]");
            return;
        } 

        // String svnUrl = "https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK";
        // String svnDir = "/Users/tocgic/SvnRepository/test-svn-git/svnOneGuard3";
        // String svnUser = null;
        // String svnPass = null;
        // String gitUrl = "git@10.0.0.69:devone/TouchEn_OneGuard_4_0_ios_SDK.git";
        // String gitDir = "/Users/tocgic/SvnRepository/test-svn-git/gitOneGuard3";
        // String gitUser = null;
        // String gitPass = null;
        GitToSvn git2svn = new GitToSvn(svnUrl, svnDir, svnUser, svnPass, gitUrl, gitDir, gitUser, gitPass);
        // git2svn.test();
        git2svn.start();
    }

    public GitToSvn(String svnUrl, String svnDir, String svnUser, String svnPass, String gitUrl, String gitDir, String gitUser, String gitPass) {
        svn = new Svn(svnUrl, svnDir, svnUser, svnPass);
        git = new Git(gitUrl, gitDir, gitUser, gitPass);
        executor = new RuntimeExecutor();
        executor.setWorkingDirectory(gitDir);
    }

    // private String run(String... commands) {
    //     String result = null;
    //     try {
    //         result = executor.execAndRtnResult(commands);
    //         Out.println(result);
    //     } catch (Exception e) {
    //         Out.println(Out.ANSI_RED, e.getMessage());
    //     }
    //     return result;
    // }

    private void cloneIfNeeds() {
        File svnFolder = new File(svn.getRepoDirectory()+File.separator+".svn");
        if (!svnFolder.exists()) {
            Out.println(Out.ANSI_YELLOW, "... cloneIfNeeds(), svn.checkout()");
            svn.checkout(true);
        }
        File gitFolder = new File(git.getRepoDirectory()+File.separator+".git");
        if (!gitFolder.exists()) {
            Out.println(Out.ANSI_YELLOW, "... cloneIfNeeds(), git.clone()");
            git.clone();
        }
    }

    private boolean cleanup() {
        Out.println(Out.ANSI_YELLOW, "... cleanup()");
        boolean result = false;
        svn.revert();
        svn.cleanup();
        git.checkout(sourceGitBranchName, true);
        result = true;
        Out.println(Out.ANSI_YELLOW, "... cleanup(), result:"+result);
        return result;
    }

    private String svnGetLastGitCommit() {
        Out.println(Out.ANSI_YELLOW, "... svnGetLastGitCommit()");
        svn.update();
        String xml = svn.getLastXmlLog();
        String commitHash = "";
        if (xml != null && xml.length() > 0 && SVN_COMMIT_TAG != null && SVN_COMMIT_TAG.length() > 0) {
            int index = xml.indexOf(SVN_COMMIT_TAG);
            int startIndex = index + SVN_COMMIT_TAG.length();
            if (index > -1 && startIndex + 40 <= xml.length()) {
                commitHash = xml.substring(startIndex, startIndex + 40);
            }
        }
        Out.println(Out.ANSI_YELLOW, "... svnGetLastGitCommit(), result:"+commitHash);
        return commitHash;
    }

    private boolean svnCheckin() {
        Out.println(Out.ANSI_YELLOW, "... svnCheckin()");
        boolean result = false;
        String svnStatus = svn.status();
        Out.println(Out.ANSI_BLUE, "... >>> svnStatus:"+svnStatus);
        if (svnStatus != null && svnStatus.length() > 0) {
            BufferedReader reader = new BufferedReader(new StringReader(svnStatus));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 1) {
                        Out.println(Out.ANSI_BLUE, "... >>> line:"+line);
                        String status = line.substring(0, 1);
                        String fileName = line.substring(1).trim();
                        if ("?".equals(status)) {
                            svn.add(fileName);
                        } else if ("!".equals(status)) {
                            svn.rm(fileName);
                        } else if ("~".equals(status)) {
                            //svn.run("rm", "-rf", fileName);
                            try {
                                FileUtils.forceDelete(new File(svn.getRepoDirectory()+File.separator+fileName));
                            } catch (IOException eFile) {
                                Out.println(Out.ANSI_RED, eFile.getMessage());
                            }
                            svn.update();
                        }
                    }
                }
                // svn.update();
                result = true;
                reader.close();
            } catch (Exception e) {
                Out.println(Out.ANSI_RED, e.getMessage());
            }
        }
        Out.println(Out.ANSI_YELLOW, "... svnCheckin(), result:"+result);
        return result;
    }

    private boolean svnCommit(String commitedDate, String commiter, String commitMessage, String commit) {
        Out.println(Out.ANSI_YELLOW, "... svnCommit()");
        boolean result = true;
        if (commitedDate == null || commitedDate.length() < 1) {
            commitedDate = "0000/00/00_00:00:00";
        }
        if (commiter == null || commiter.length() < 1) {
            commiter = "unknown";
        }
        if (commitMessage == null || commitMessage.length() < 1) {
            commitMessage = "";
        }
        String authorInfo = "["+commiter+"]";
        String message = commitMessage.replace(authorInfo, "").trim()+"\n\n"+SVN_COMMIT_TAG+commit;
        String response = svn.commit(commitedDate+" "+authorInfo+" "+message);
        if (response != null && response.length() > 0) {
            if (response.contains("error")) {
                Out.println(Out.ANSI_RED, response);
                result = false;
            }
        }
        Out.println(Out.ANSI_YELLOW, "... svnCommit(), result:"+result);
        return result;
    }

    private ArrayList<String> getGitAllRevList() {
        Out.println(Out.ANSI_YELLOW, "... getGitAllRevList()");
        ArrayList<String> revList = new ArrayList<>();
        String response = git.getRevListAllMatch(sourceGitBranchName);
        if (response != null && response.length() > 0) {
            BufferedReader reader = new BufferedReader(new StringReader(response));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    revList.add(line.trim());
                }
                reader.close();
            } catch (Exception e) {
                Out.println(Out.ANSI_RED, e.getMessage());
            }
        }
        Out.println(Out.ANSI_YELLOW, "... getGitAllRevList(), itemCount:"+revList.size());
        return revList;
    }

    private void copyGitToSvn() {
        boolean isIgnoreGitSpecificFiles = false;
        Out.println(Out.ANSI_YELLOW, "... copyGitToSvn(isIgnoreGitSpecificFiles:"+isIgnoreGitSpecificFiles+")");

        File gitDir = new File(git.getRepoDirectory());
        File svnDir = new File(svn.getRepoDirectory());
        
        try {
            //#rm -rf $SVN_DIR/*;
            IOFileFilter rmIgnoreFilter = new IOFileFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !dir.isHidden();
                }
                @Override
                public boolean accept(File file) {
                    return !file.isHidden();
                }
            };
            Collection<File> allFiles = FileUtils.listFilesAndDirs(svnDir, rmIgnoreFilter, rmIgnoreFilter);
            for (File file : allFiles) {
                if (file.getAbsolutePath() == svnDir.getAbsolutePath()) {
                    continue;
                }
                if (!file.exists()) {
                    continue;
                }
                Out.println(Out.ANSI_YELLOW, "... deleteFile : "+file.getAbsolutePath());
                FileUtils.forceDelete(file);
            }
            
            //#cp -prf $GIT_DIR/* $SVN_DIR/; & # Remove Git specific files from SVN
            FileFilter cpIgnoreFilter = new FileFilter(){
                @Override
                public boolean accept(File pathname) {
                    if (pathname != null) {
                        String fullPath = pathname.getAbsolutePath();
                        if (fullPath != null && fullPath.length() > 0 && (fullPath.contains(".git") || fullPath.contains(".gitignore"))) {
                            return false;
                        }
                    }
                    return true;
                }
            };
            FileUtils.copyDirectory(gitDir, svnDir, cpIgnoreFilter, true);
        } catch (Exception e) {
            Out.println(Out.ANSI_RED, e.getMessage());
        }
    }

    public void test() {
        // svn.getLastXmlLog();
        // git.checkout("master", true);
        // Out.println(git.getLogValueAuthor("02acdd5181b98c3a471a9b36e2450fb91eb284df"));
        // Out.println(git.getLogValueMsg("02acdd5181b98c3a471a9b36e2450fb91eb284df"));
        // Out.println(git.getLogValueDate("02acdd5181b98c3a471a9b36e2450fb91eb284df"));

        cloneIfNeeds();

        cleanup();

        // String svnLastGitCommit = svnGetLastGitCommit();
        // String svnLastGitCommit = "224da1d9d0cd87ae3c493a5fd8e865634d961d58";
        // ArrayList<String> revList = getGitAllRevList();
        // if (svnLastGitCommit.length() == 40) {
        //     int index = revList.indexOf(svnLastGitCommit);
        //     if (index > -1) {
        //         int oldSize = revList.size();
        //         for (int i = 0; i <= index; i++) {
        //             revList.remove(0);
        //         }
        //         Out.println(Out.ANSI_BLUE, "... find last git commit on svn. (index:"+index+"), oldSize:"+oldSize+", currentSize:"+revList.size());
        //         for (String commit : revList) {
        //             Out.println(commit);
        //         }
        //     }
        // }

        // Out.println(Out.ANSI_BLUE, "STEP 3. loop start (git to svn)");
        // for (String commit : revList) {

            // String commit = "02acdd5181b98c3a471a9b36e2450fb91eb284df";
            // String commit = "4e08059f7fa1965cd9d2f410ed998a489fd14ef1";
            // String commit = "224da1d9d0cd87ae3c493a5fd8e865634d961d58";
            String commit = "d9917d7aaf49d3ac5e01bc76f156ce5399d6df88";

            // Out.println(Out.ANSI_BLUE, "... checking out commit["+commit+"] on git");
            git.checkout(commit, true);

            Out.println(Out.ANSI_BLUE, "... copying files");
            copyGitToSvn();

            String svnStatus = svn.status();
            Out.println(Out.ANSI_BLUE, "... >>> svnStatus:"+svnStatus);
    
            // // Out.println(Out.ANSI_BLUE, "... remove Git specific files from SVN");

            // // Out.println(Out.ANSI_BLUE, "... add new files to SVN and commit");
            svnCheckin();
            String commiter = git.getLogValueAuthor(commit);
            String commitedDate = git.getLogValueDate(commit);
            String commitMessage = git.getLogValueMsg(commit);
            svnCommit(commitedDate, commiter, commitMessage, commit);
        // }
    }

    public void start() {
        Out.println(Out.ANSI_BLUE, "STEP 1. clone (svn & git)");
        cloneIfNeeds();
        
        Out.println(Out.ANSI_BLUE, "STEP 2. clean");
        cleanup();
        Out.println(Out.ANSI_BLUE, "STEP 3. check lastGitCommit on svn");
        String svnLastGitCommit = svnGetLastGitCommit();
        ArrayList<String> revList = getGitAllRevList();
        if (svnLastGitCommit.length() == 40) {
            int index = revList.indexOf(svnLastGitCommit);
            if (index > -1) {
                int oldSize = revList.size();
                for (int i = 0; i <= index; i++) {
                    revList.remove(0);
                }
                Out.println(Out.ANSI_BLUE, "... find last git commit on svn. (index:"+index+"), oldSize:"+oldSize+", currentSize:"+revList.size());
            }
        }
        Out.println(Out.ANSI_BLUE, "STEP 4. loop start (git to svn)");
        for (String commit : revList) {
            String commiter = git.getLogValueAuthor(commit);
            String commitedDate = git.getLogValueDate(commit);
            String commitMessage = git.getLogValueMsg(commit);

            Out.println(Out.ANSI_BLUE, "... checking out commit["+commit+"] on git");
            git.checkout(commit, true);

            Out.println(Out.ANSI_BLUE, "... copying files");
            copyGitToSvn();

            Out.println(Out.ANSI_BLUE, "... add new files to SVN and commit");
            svnCheckin();
            svnCommit(commitedDate, commiter, commitMessage, commit);
        }
    }
        
        
}