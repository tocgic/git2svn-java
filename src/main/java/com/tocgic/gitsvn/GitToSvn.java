package com.tocgic.gitsvn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import com.tocgic.gitsvn.util.Out;
import com.tocgic.gitsvn.util.RuntimeExecutor;
import com.tocgic.gitsvn.versioncontrolservice.Git;
import com.tocgic.gitsvn.versioncontrolservice.Svn;

public class GitToSvn {
    private static final int RETRY_MAX = 3;
    private static final String SVN_COMMIT_TAG = "GitCommitHash:";
    private Svn svn;
    private Git git;
    private String sourceGitBranchName = "master";
    private boolean isDebug;
    private int retryLimit = RETRY_MAX;
    
    public static void main(String args[]) {
        String svnUrl = null;
        String svnDir = null;
        String svnUser = null;
        String svnPass = null;
        String gitUrl = null;
        String gitDir = null;
        String gitUser = null;
        String gitPass = null;

        int index = 0;
        boolean optionIsDebug = false;
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith("-")) {
                    index++;
                    if ("-debug".equals(arg)) {
                        optionIsDebug = true;
                    }
                }
            }
        }

        if (args != null && args.length == index + 2) {
            gitDir = args[index++];
            svnDir = args[index++];
        } else if (args != null && args.length == index + 4) {
            gitDir = args[index++];
            gitUrl = args[index++];
            svnDir = args[index++];
            svnUrl = args[index++];
        } else if (args != null && args.length == index + 5) {
            gitDir = args[index++];
            svnDir = args[index++];
            svnUrl = args[index++];
            svnUser = args[index++];
            svnPass = args[index++];
        } else if (args != null && args.length == index + 6) {
            gitDir = args[index++];
            gitUrl = args[index++];
            svnDir = args[index++];
            svnUrl = args[index++];
            svnUser = args[index++];
            svnPass = args[index++];
        } else if (args != null && args.length == index + 8) {
            gitDir = args[index++];
            gitUrl = args[index++];
            gitUser = args[index++];
            gitPass = args[index++];
            svnDir = args[index++];
            svnUrl = args[index++];
            svnUser = args[index++];
            svnPass = args[index++];
        } else {
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [option:-debug] [gitDir] [svnDir]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"/Users/tocgic/tempTargetSvn\"");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [option:-debug] [gitDir] [gitUrl] [svnDir] [svnUrl]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"git@10.0.0.69:devone/TouchEn_OneGuard_4_0_ios_SDK.git\" \"/Users/tocgic/temp/sourceSvn\" \"https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK\"");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [option:-debug] [gitDir] [svnDir] [svnUrl] [svnUser] [svnPass]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"/Users/tocgic/temp/sourceSvn\" \"https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK\" svnUserId svnPassword");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [option:-debug] [gitDir] [gitUrl] [svnDir] [svnUrl] [svnUser] [svnPass]");
            Out.println(Out.ANSI_BLUE, "exam$ java -jar git2svn-java.jar \"/Users/tocgic/temp/sourceGit\" \"git@10.0.0.69:devone/TouchEn_OneGuard_4_0_ios_SDK.git\" \"/Users/tocgic/temp/sourceSvn\" \"https://dwhan@pms.raonsecure.com:8000/svn/TouchEn_mGuard/agent/TouchEn_mGuard_3_5/iOS/trunk/OneGuardSDK\" svnUserId svnPassword");
            Out.println(Out.ANSI_YELLOW, "usage: java -jar git2svn-java.jar [option:-debug] [gitDir] [gitUrl] [gitUser] [gitPass] [svnDir] [svnUrl] [svnUser] [svnPass]");
            return;
        } 

        GitToSvn git2svn = new GitToSvn(svnUrl, svnDir, svnUser, svnPass, gitUrl, gitDir, gitUser, gitPass);
        git2svn.setDebug(optionIsDebug);

        // git2svn.test();
        git2svn.start();
    }

    public GitToSvn(String svnUrl, String svnDir, String svnUser, String svnPass, String gitUrl, String gitDir, String gitUser, String gitPass) {
        svn = new Svn(svnUrl, svnDir, svnUser, svnPass);
        git = new Git(gitUrl, gitDir, gitUser, gitPass);
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        svn.setDebug(isDebug);
        git.setDebug(isDebug);
    }

    private void cloneIfNeeds() {
        File svnFolder = new File(svn.getRepoDirectory()+File.separator+".svn");
        if (!svnFolder.exists()) {
            Out.println(Out.ANSI_YELLOW, "cloneIfNeeds(), svn.checkout()");
            svn.checkout(true);
        }
        File gitFolder = new File(git.getRepoDirectory()+File.separator+".git");
        if (!gitFolder.exists()) {
            Out.println(Out.ANSI_YELLOW, "cloneIfNeeds(), git.clone()");
            git.clone();
        }
    }

    private boolean cleanup() {
        Out.println(Out.ANSI_YELLOW, "cleanup()");
        boolean result = false;
        svn.revert();
        svn.cleanup(true);
        String response = git.checkout(sourceGitBranchName, true);
        result = RuntimeExecutor.isErrorResponse(response);
        if (isDebug) {
            Out.println(Out.ANSI_YELLOW, "cleanup(), result:"+result);
        }
        return result;
    }

    private String svnGetLastGitCommit() {
        Out.println(Out.ANSI_YELLOW, "svnGetLastGitCommit()");
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
        if (isDebug) {
            Out.println(Out.ANSI_YELLOW, "svnGetLastGitCommit(), result:"+commitHash);
        }
        return commitHash;
    }

    private boolean svnCheckin() {
        Out.println(Out.ANSI_YELLOW, "svnCheckin()");
        boolean result = false;
        String svnStatus = svn.status();
        if (svnStatus != null && svnStatus.length() > 0) {
            BufferedReader reader = new BufferedReader(new StringReader(svnStatus));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 1) {
                        //Out.println(Out.ANSI_BLUE, ">>> line:"+line);
                        String[] items = line.split(" ");
                        String status = items[0];
                        // String fileName = line.substring(status.length()).trim().replace(" ", "\\ ");
                        String fileName = line.substring(status.length()).trim();
                        if ("?".equals(status)) {
                            svn.add(fileName);
                        } else if (status != null && status.startsWith("!")) {
                            svn.rm(fileName);
                        } else if (status != null && status.startsWith("~")) {
                            //svn.run("rm", "-rf", fileName);
                            try {
                                FileUtils.forceDelete(new File(svn.getRepoDirectory()+File.separator+fileName));
                            } catch (IOException eFile) {
                                Out.println(Out.ANSI_RED, eFile.getMessage());
                            }
                            svn.update();
                        } else {
                            if (status.length()> 1) {
                                Out.println(Out.ANSI_RED, "!!! svn status:"+status+", fileName:"+fileName);
                                Out.println(Out.ANSI_RED, "!!! Check svn status\n"+svnStatus);
                                System.exit(1);
                                break;
                            }
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
        if (isDebug) {
            Out.println(Out.ANSI_YELLOW, "svnCheckin(), result:"+result);
        }
        return result;
    }

    private boolean svnCommit(String commitedDate, String commiter, String commitMessage, String commit) {
        Out.println(Out.ANSI_YELLOW, "svnCommit()");
        int tryLimit = 1;
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
        commitMessage = commitMessage.replaceAll("\r", "\n");
        String authorInfo = "["+commiter+"]";
        String message = commitMessage.replace(authorInfo, "").trim()+"\n\n"+SVN_COMMIT_TAG+commit;
        String newCommitMessage = commitedDate+" "+authorInfo+" "+message;
        do {
            tryLimit--;
            String response = svn.commit(newCommitMessage);
            if (RuntimeExecutor.isErrorResponse(response)) {
                Out.println(Out.ANSI_RED, response);
                result = false;
                Out.println(Out.ANSI_YELLOW, "svnCommit() - svn.clean() for RETRY");
                svn.cleanup(false);
                //svn.update();
                Out.println(Out.ANSI_BLUE, ">>> commite Infos >>>");
                Out.println(Out.ANSI_BLUE, commitedDate);
                Out.println(Out.ANSI_BLUE, authorInfo);
                Out.println(Out.ANSI_BLUE, message);
                Out.println(Out.ANSI_BLUE, "\n");
                try {
                    Thread.sleep(500L);
                } catch (Exception e) {}
            } else {
                break;
            }
        } while (tryLimit > 0);
        Out.println(Out.ANSI_YELLOW, "svnCommit(), result:"+result);
        Out.println(Out.ANSI_YELLOW, "======================================================================\n"+newCommitMessage+"\n======================================================================\n\n");
        return result;
    }

    private ArrayList<String> getGitAllRevList() {
        Out.println(Out.ANSI_YELLOW, "getGitAllRevList()");
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
        Out.println(Out.ANSI_YELLOW, "getGitAllRevList(), itemCount:"+revList.size());
        return revList;
    }

    private void copyGitToSvn() {
        Out.println(Out.ANSI_YELLOW, "copyGitToSvn()");

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
                //Out.println(Out.ANSI_YELLOW, "deleteFile : "+file.getAbsolutePath());
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
        //         Out.println(Out.ANSI_BLUE, "find last git commit on svn. (index:"+index+"), oldSize:"+oldSize+", currentSize:"+revList.size());
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

            // Out.println(Out.ANSI_BLUE, "checking out commit["+commit+"] on git");
            git.checkout(commit, true);

            Out.println(Out.ANSI_BLUE, "copying files");
            copyGitToSvn();

            String svnStatus = svn.status();
            Out.println(Out.ANSI_BLUE, ">>> svnStatus:"+svnStatus);
    
            // // Out.println(Out.ANSI_BLUE, "remove Git specific files from SVN");

            // // Out.println(Out.ANSI_BLUE, "add new files to SVN and commit");
            svnCheckin();
            String commiter = git.getLogValueAuthor(commit);
            String commitedDate = git.getLogValueDate(commit);
            String commitMessage = git.getLogValueMsg(commit);
            svnCommit(commitedDate, commiter, commitMessage, commit);
        // }
    }
    
    private void start() {
        Date startDate = new java.util.Date();
        Out.println(Out.ANSI_PURPLE, ">> Check (svn & git) repository directory.");
        cloneIfNeeds();
        boolean isSuccessDone = false;
        int totalRetryCount = 0;
        do {
            Out.println(Out.ANSI_PURPLE, ">> Clean (svn & git) repository.");
            cleanup();
            Out.println(Out.ANSI_PURPLE, ">> Check lastGitCommit on svn");
            String svnLastGitCommit = svnGetLastGitCommit();
            ArrayList<String> revList = getGitAllRevList();
            if (svnLastGitCommit.length() == 40) {
                int index = revList.indexOf(svnLastGitCommit);
                if (index > -1) {
                    int oldSize = revList.size();
                    for (int i = 0; i <= index; i++) {
                        revList.remove(0);
                    }
                    Out.println(Out.ANSI_YELLOW, "- find last git commit on svn. (index:"+index+"), totalCount:"+oldSize+", todoCount:"+revList.size());
                }
            }
            Out.println(Out.ANSI_PURPLE, ">> Start loop (git to svn)");
            for (String commit : revList) {
                Out.println(Out.ANSI_PURPLE, ">> Process Git ["+commit+"]");
                String commiter = git.getLogValueAuthor(commit);
                String commitMessage = git.getLogValueMsg(commit);
                String commitedDate = git.getLogValueDate(commit);
                if (commitedDate != null && commitedDate.length() > 0) {
                    commitedDate = commitedDate.replace("_", " ");
                }

                Out.println(Out.ANSI_PURPLE, "- GIT, checking out commit["+commit+"]");
                git.checkout(commit, true);

                Out.println(Out.ANSI_PURPLE, "- GIT -> SVN,  Copying files");
                copyGitToSvn();

                Out.println(Out.ANSI_PURPLE, "- SVN, add new files to SVN and commit");
                svnCheckin();
                boolean commitResult = svnCommit(commitedDate, commiter, commitMessage, commit);
                if (!commitResult) {
                    Out.println(Out.ANSI_RED, "!!! svn commit FAIL !!! git commit:"+commit);
                    retryLimit--;
                    totalRetryCount++;
                    break;
                } else {
                    retryLimit = RETRY_MAX;
                }
            }
            isSuccessDone = retryLimit == RETRY_MAX;
        } while (!isSuccessDone && retryLimit > 0);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new java.util.Date();
        String colorValue = isSuccessDone ? Out.ANSI_YELLOW : Out.ANSI_RED;        
        Out.println(colorValue, "======================================================================");
        Out.println(colorValue, "... F I N - "+ (isSuccessDone ? "S U C C E S S" : "F A I L"));
        Out.println(colorValue, "======================================================================");
        Out.println(colorValue, "... totalRetryCount : "+ totalRetryCount);
        Out.println(colorValue, "... ["+dateFormat.format(startDate)+"] - start");
        Out.println(colorValue, "... ["+dateFormat.format(date)+"] - end");
    }
}